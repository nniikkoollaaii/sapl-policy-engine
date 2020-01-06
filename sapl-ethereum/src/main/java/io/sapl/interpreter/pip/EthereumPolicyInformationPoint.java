package io.sapl.interpreter.pip;

import static io.sapl.interpreter.pip.EthereumPipFunctions.convertToType;
import static io.sapl.interpreter.pip.EthereumPipFunctions.extractDefaultBlockParameter;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.Transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.AttributeException;
import io.sapl.api.pip.PolicyInformationPoint;
import reactor.core.publisher.Flux;

/**
 * The Ethereum Policy Information Point gives access to most methods of the JSON-RPC Ethereum API
 * (https://github.com/ethereum/wiki/wiki/JSON-RPC)
 *
 * Excluded are all methods that would change the state of the blockchain as it doesn't make sense to use them during a
 * policy evaluation. These methods are eth_sendTransaction, eth_sendRawTransaction, eth_submitWork and
 * eth_submitHashrate. The methods that are changing something in the node are excluded, because creating or managing
 * filters and shh identities should not be done inside a policy. These methods are eth_newFilter, eth_newBlockFilter,
 * eth_newPendingTransactionFilter, eth_uninstallFilter, shh_post, shh_newIdentity, shh_addToGroup, shh_newFilter and
 * shh_uninstallFilter. Also excluded are the deprecated methods eth_getCompilers, eth_compileSolidity, eth_compileLLL
 * and eth_compileSerpent. Further excluded are all db_ methods as they are deprecated and will be removed. Also
 * excluded is the eth_getProof method as at time of writing this there doesn't exist an implementation in the Web3j
 * API.
 *
 * Finally the methods verifyTransaction and loadContractInformation are not part of the JSON RPC API but are considered
 * to be a more user friendly implementation of the most common use cases.
 */

@PolicyInformationPoint(name = "ethereum", description = "Connects to the Ethereum Blockchain.")
public class EthereumPolicyInformationPoint {

	private static final long DEFAULT_ETH_POLLING_INTERVAL = 5000L;

	private static final String ADDRESS = "address";

	private static final String CONTRACT_ADDRESS = "contractAddress";

	private static final String TRANSACTION_HASH = "transactionHash";

	private static final String FROM_ACCOUNT = "fromAccount";

	private static final String TO_ACCOUNT = "toAccount";

	private static final String TRANSACTION_VALUE = "transactionValue";

	private static final String INPUT_PARAMS = "inputParams";

	private static final String OUTPUT_PARAMS = "outputParams";

	private static final String FUNCTION_NAME = "functionName";

	private static final String POSITION = "position";

	private static final String BLOCK_HASH = "blockHash";

	private static final String SHA3_HASH_OF_DATA_TO_SIGN = "sha3HashOfDataToSign";

	private static final String TRANSACTION = "transaction";

	private static final String RETURN_FULL_TRANSACTION_OBJECTS = "returnFullTransactionObjects";

	private static final String TRANSACTION_INDEX = "transactionIndex";

	private static final String FILTER_ID = "filterId";

	private static final String VERIFY_TRANSACTION_WARNING = "There was an error during verifyTransaction. By default false is returned but the transaction could have taken place.";

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static final Logger LOGGER = LoggerFactory.getLogger(EthereumPolicyInformationPoint.class);

	private final Flux<Long> timer;

	private Web3j web3j;

	public EthereumPolicyInformationPoint(Web3jService web3jService) {
		this(web3jService, DEFAULT_ETH_POLLING_INTERVAL);
	}

	public EthereumPolicyInformationPoint(Web3jService web3jService, long ethereumPollingIntervalInMs) {
		web3j = Web3j.build(web3jService);
		timer = Flux.interval(Duration.ZERO, Duration.ofMillis(ethereumPollingIntervalInMs));
	}

	/**
	 * Method for verifying if a given transaction has taken place.
	 * @param saplObject needs to have the following values: <br>
	 * "transactionHash" : The hash of the transaction that should be verified <br>
	 * "fromAccount" : The adress of the account the transaction is send from <br>
	 * "toAccount" : The adress of the account that receives the transaction <br>
	 * "transactionValue" : A BigInteger that represents the value of the transaction in Wei
	 * @param variables is unused here
	 * @return A Flux of JsonNodes that have boolean value true if the transaction has taken place and false otherwise @
	 */
	@Attribute(name = "transaction", docs = "Returns true, if a transaction has taken place and false otherwise.")
	public Flux<JsonNode> verifyTransaction(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withVerifiedTransaction(saplObject));
	}

	private Callable<JsonNode> withVerifiedTransaction(JsonNode saplObject) {
		return () -> {
			try {
				Optional<Transaction> optionalTransactionFromChain = web3j
						.ethGetTransactionByHash(getStringFrom(saplObject, TRANSACTION_HASH)).send().getTransaction();
				if (optionalTransactionFromChain.isPresent()) {
					Transaction transactionFromChain = optionalTransactionFromChain.get();
					if (transactionFromChain.getFrom().toLowerCase()
							.equals(getStringFrom(saplObject, FROM_ACCOUNT).toLowerCase())
							&& transactionFromChain.getTo().toLowerCase()
									.equals(getStringFrom(saplObject, TO_ACCOUNT).toLowerCase())
							&& transactionFromChain.getValue().equals(getBigIntFrom(saplObject, TRANSACTION_VALUE))) {
						return JSON.booleanNode(true);
					}
				}
			}
			catch (IOException | NullPointerException e) {
				LOGGER.warn(VERIFY_TRANSACTION_WARNING);
			}
			return JSON.booleanNode(false);
		};
	}

	/**
	 * Method for querying the state of a contract.
	 * @param saplObject needs to have the following values <br>
	 * "fromAccount" : (Optional) The account that makes the request <br>
	 * "contractAddress" : The address of the called contract <br>
	 * "functionName" : The name of the called function. <br>
	 * "inputParams" : A Json ArrayNode that contains a tuple of "type" and "value" for each input parameter. Example:
	 * [{"type" : "uint32", "value" : 45},{"type" : "bool", "value" : "true"}] <br>
	 * "outputParams" : A Json ArrayNode that contains the return types. Example: ["address","bool"] <br>
	 * All types that can be used are listed in the convertToType-method of the <a href=
	 * "https://github.com/heutelbeck/sapl-policy-engine/blob/sapl-ethereum/sapl-ethereum/src/main/java/io/sapl/interpreter/pip/EthereumPipFunctions.java">EthereumPipFunctions</a>.
	 * @param variables is unused here
	 * @return A Flux of ArrayNodes that contain the return value(s) of the called contract function. Each node entry
	 * contains two values, "value" with the return value and "typeAsString" with the return type. Example for a return
	 * array: [{"value":true,"typeAsString":"bool"}, {"value":324,"typeAsString":"uint"}] @
	 */
	@Attribute(name = "contract", docs = "Returns the result of a function call of a specified contract.")
	public Flux<JsonNode> loadContractInformation(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withInformationFromContract(saplObject));
	}

	private Callable<JsonNode> withInformationFromContract(JsonNode saplObject) {
		return () -> {
			String fromAccount = getStringFrom(saplObject, FROM_ACCOUNT);
			String contractAddress = getStringFrom(saplObject, CONTRACT_ADDRESS);

			List<Type> inputParameters = new ArrayList<>();
			JsonNode inputNode = saplObject.get(INPUT_PARAMS);
			if (inputNode.isArray()) {
				for (JsonNode inputParam : inputNode) {
					inputParameters.add(convertToType(inputParam));
				}
			}
			try {

				List<TypeReference<?>> outputParameters = new ArrayList<>();
				JsonNode outputNode = saplObject.get(OUTPUT_PARAMS);
				if (outputNode.isArray()) {
					for (JsonNode solidityType : outputNode) {
						outputParameters.add(TypeReference.makeTypeReference(solidityType.textValue()));
					}
				}

				org.web3j.abi.datatypes.Function function = new Function(getStringFrom(saplObject, FUNCTION_NAME),
						inputParameters, outputParameters);
				String encodedFunction = FunctionEncoder.encode(function);
				EthCall response = web3j
						.ethCall(
								org.web3j.protocol.core.methods.request.Transaction
										.createEthCallTransaction(fromAccount, contractAddress, encodedFunction),
								extractDefaultBlockParameter(saplObject))
						.send();
				List<Type> output = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

				return convertToJsonNode(output);

			}
			catch (IOException | ClassNotFoundException e) {
				throw new AttributeException(e);
			}
		};
	}

	/**
	 * This simply returns the version of the client running the node that the EthPip connects to.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return A Flux of JsonNodes containing a string with the clientVersion
	 */
	@Attribute(name = "clientVersion", docs = "Returns the current client version.")
	public Flux<JsonNode> web3ClientVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withWeb3ClientVersion());
	}

	private Callable<JsonNode> withWeb3ClientVersion() {

		return () -> convertToJsonNode(web3j.web3ClientVersion().send().getWeb3ClientVersion());

	}

	/**
	 * Thie function can be used to get the Keccak-256 Hash (which is commonly used in Ethereum) of a given hex value.
	 * @param saplObject should contain only a string that has to be a hex value, otherwise the hash can't be
	 * calculated.
	 * @param variables is unused here
	 * @return Flux of JsonNodes containing a string with the hash value of the data.
	 */
	@Attribute(name = "sha3", docs = "Returns Keccak-256 (not the standardized SHA3-256) of the given data.")
	public Flux<JsonNode> web3Sha3(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withWeb3Sha3(saplObject));

	}

	private Callable<JsonNode> withWeb3Sha3(JsonNode saplObject) {
		return () -> convertToJsonNode(web3j.web3Sha3(saplObject.textValue()).send().getResult());
	}

	/**
	 * Method for querying the id of the network the client is connected to. Common network ids are 1 for the Ethereum
	 * Mainnet, 3 for Ropsten Tesnet, 4 for Rinkeby testnet and 42 for Kovan Testnet. Any other id most probably refers
	 * to a private testnet.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes containing a string with the current network id.
	 */
	@Attribute(name = "netVersion", docs = "Returns the current network id.")
	public Flux<JsonNode> netVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withNetVersion());

	}

	private Callable<JsonNode> withNetVersion() {

		return () -> convertToJsonNode(web3j.netVersion().send().getNetVersion());

	}

	/**
	 * A simple method that checks if the client is listening for network connections.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes with boolean value true if listening and false otherwise.
	 */
	@Attribute(name = "listening", docs = "Returns true if client is actively listening for network connections.")
	public Flux<JsonNode> netListening(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withNetListening());

	}

	private Callable<JsonNode> withNetListening() {

		return () -> convertToJsonNode(web3j.netListening().send().isListening());

	}

	/**
	 * Method to find out the number of connected peers.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes with the number of connected peers as BigInteger value.
	 */
	@Attribute(name = "peerCount", docs = "Returns number of peers currently connected to the client.")
	public Flux<JsonNode> netPeerCount(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withNetPeerCount());

	}

	private Callable<JsonNode> withNetPeerCount() {

		return () -> convertToJsonNode(web3j.netPeerCount().send().getQuantity());

	}

	/**
	 * Method for querying the version of the currently used ethereum protocol.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes that contain the protocol version as a String
	 */
	@Attribute(name = "protocolVersion", docs = "Returns the current ethereum protocol version.")
	public Flux<JsonNode> ethProtocolVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthProtocolVersion());

	}

	private Callable<JsonNode> withEthProtocolVersion() {

		return () -> convertToJsonNode(web3j.ethProtocolVersion().send().getProtocolVersion());

	}

	/**
	 * Simple method to check if the client is currently syncing with the network.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes with boolean value true if syncing and false otherwise.
	 */
	@Attribute(name = "syncing", docs = "Returns true if the client is syncing or false otherwise.")
	public Flux<JsonNode> ethSyncing(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthSyncing());

	}

	private Callable<JsonNode> withEthSyncing() {

		return () -> convertToJsonNode(web3j.ethSyncing().send().isSyncing());

	}

	/**
	 * Method for retrieving the address of the client coinbase.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes containing the address of the client coinbase as a String.
	 */
	@Attribute(name = "coinbase", docs = "Returns the client coinbase address.")
	public Flux<JsonNode> ethCoinbase(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthCoinbase());

	}

	private Callable<JsonNode> withEthCoinbase() {

		return () -> convertToJsonNode(web3j.ethCoinbase().send().getResult());

	}

	/**
	 * Simple method to check if the client is mining.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes with boolean value true if mining and false otherwise.
	 */
	@Attribute(name = "mining", docs = "Returns true if client is actively mining new blocks.")
	public Flux<JsonNode> ethMining(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthMining());

	}

	private Callable<JsonNode> withEthMining() {

		return () -> convertToJsonNode(web3j.ethMining().send().isMining());

	}

	/**
	 * Method for querying the number of hashes per second that the client is mining with.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes with the hashrate as BigInteger value.
	 */
	@Attribute(name = "hashrate", docs = "Returns the number of hashes per second that the node is mining with.")
	public Flux<JsonNode> ethHashrate(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthHashrate());

	}

	private Callable<JsonNode> withEthHashrate() {

		return () -> convertToJsonNode(web3j.ethHashrate().send().getHashrate());

	}

	/**
	 * Method for querying the current gas price in wei.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes containing the gas price as BigInteger value.
	 */
	@Attribute(name = "gasPrice", docs = "Returns the current price per gas in wei.")
	public Flux<JsonNode> ethGasPrice(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthGasPrice());

	}

	private Callable<JsonNode> withEthGasPrice() {

		return () -> convertToJsonNode(web3j.ethGasPrice().send().getGasPrice());

	}

	/**
	 * Method for returning all addresses owned by the client.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of ArrayNodes that contain the owned addresses as Strings.
	 */
	@Attribute(name = "accounts", docs = "Returns a list of addresses owned by client.")
	public Flux<JsonNode> ethAccounts(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthAccounts());

	}

	private Callable<JsonNode> withEthAccounts() {

		return () -> convertToJsonNode(web3j.ethAccounts().send().getResult());

	}

	/**
	 * Method for receiving the number of the most recent block.
	 * @param saplObject is unused here
	 * @param variables is unused here
	 * @return Flux of JsonNodes containing the blocknumber as BigInteger.
	 */
	@Attribute(name = "blockNumber", docs = "Returns the number of most recent block.")
	public Flux<JsonNode> ethBlockNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEthBlockNumber());

	}

	private Callable<JsonNode> withEthBlockNumber() {

		return () -> convertToJsonNode(web3j.ethBlockNumber().send().getBlockNumber());

	}

	/**
	 * Method for querying the balance of an account at a given block. If no DefaultBlockParameter is provided the
	 * latest Block will be queried.
	 * @param saplObject needs to have the following values: <br>
	 * "address": The address of the account that you want to get the balance of. <br>
	 * An optional DefaultBlockParameter. More information on how to provide it can be found in the
	 * extractDefaultBlockParameter method.
	 * @param variables
	 * @return Flux of JsonNodes holding the balance in wei as BigInteger.
	 * @see io.sapl.interpreter.pip.EthereumPipFunctions#extractDefaultBlockParameter(JsonNode)
	 * extractDefaultBlockParameter
	 */
	@Attribute(name = "balance", docs = "Returns the balance of the account of given address.")
	public Flux<JsonNode> ethGetBalance(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withAccountBalance(saplObject));

	}

	private Callable<JsonNode> withAccountBalance(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetBalance(getStringFrom(saplObject, ADDRESS), extractDefaultBlockParameter(saplObject)).send()
						.getBalance());

	}

	@Attribute(name = "storage", docs = "Returns the value from a storage position at a given address.")
	public Flux<JsonNode> ethGetStorageAt(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withStorageAt(saplObject));

	}

	private Callable<JsonNode> withStorageAt(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetStorageAt(getStringFrom(saplObject, ADDRESS),
				saplObject.get(POSITION).bigIntegerValue(), extractDefaultBlockParameter(saplObject)).send().getData());

	}

	@Attribute(name = "transactionCount", docs = "Returns the number of transactions sent from an address.")
	public Flux<JsonNode> ethGetTransactionCount(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withTransactionCount(saplObject));

	}

	private Callable<JsonNode> withTransactionCount(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j
				.ethGetTransactionCount(getStringFrom(saplObject, ADDRESS), extractDefaultBlockParameter(saplObject))
				.send().getTransactionCount());

	}

	@Attribute(name = "blockTransactionCountByHash", docs = "Returns the number of transactions in a block from a block matching the given block hash.")
	public Flux<JsonNode> ethGetBlockTransactionCountByHash(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withBlockTransactionCountByHash(saplObject));

	}

	private Callable<JsonNode> withBlockTransactionCountByHash(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetBlockTransactionCountByHash(getStringFrom(saplObject, BLOCK_HASH))
				.send().getTransactionCount());

	}

	@Attribute(name = "blockTransactionCountByNumber", docs = "Returns the number of transactions in a block matching the given block number.")
	public Flux<JsonNode> ethGetBlockTransactionCountByNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withBlockTransactionCountByNumber(saplObject));

	}

	private Callable<JsonNode> withBlockTransactionCountByNumber(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetBlockTransactionCountByNumber(extractDefaultBlockParameter(saplObject)).send()
						.getTransactionCount());

	}

	@Attribute(name = "uncleCountByBlockHash", docs = "Returns the number of uncles in a block from a block matching the given block hash.")
	public Flux<JsonNode> ethGetUncleCountByBlockHash(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withUncleCountByBlockHash(saplObject));

	}

	private Callable<JsonNode> withUncleCountByBlockHash(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetUncleCountByBlockHash(getStringFrom(saplObject, BLOCK_HASH)).send().getUncleCount());

	}

	@Attribute(name = "uncleCountByBlockNumber", docs = "Returns the number of uncles in a block from a block matching the given block number.")
	public Flux<JsonNode> ethGetUncleCountByBlockNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withUncleCountByBlockNumber(saplObject));

	}

	private Callable<JsonNode> withUncleCountByBlockNumber(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetUncleCountByBlockNumber(extractDefaultBlockParameter(saplObject)).send().getUncleCount());

	}

	@Attribute(name = "code", docs = "Returns code at a given address.")
	public Flux<JsonNode> ethGetCode(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withCode(saplObject));

	}

	private Callable<JsonNode> withCode(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetCode(getStringFrom(saplObject, ADDRESS), extractDefaultBlockParameter(saplObject)).send()
						.getCode());

	}

	@Attribute(name = "sign", docs = "The sign method calculates an Ethereum specific signature.")
	public Flux<JsonNode> ethSign(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withSignature(saplObject));

	}

	private Callable<JsonNode> withSignature(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethSign(getStringFrom(saplObject, ADDRESS), getStringFrom(saplObject, SHA3_HASH_OF_DATA_TO_SIGN))
						.send().getSignature());

	}

	@Attribute(name = "call", docs = "Executes a new message call immediately without creating a transaction on the block chain.")
	public Flux<JsonNode> ethCall(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withCallResult(saplObject));

	}

	private Callable<JsonNode> withCallResult(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethCall(
				mapper.convertValue(saplObject.get(TRANSACTION),
						org.web3j.protocol.core.methods.request.Transaction.class),
				extractDefaultBlockParameter(saplObject)).send().getResult());

	}

	@Attribute(name = "estimateGas", docs = "Generates and returns an estimate of how much gas is necessary to allow the transaction to complete.")
	public Flux<JsonNode> ethEstimateGas(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withEstimatedGas(saplObject));

	}

	private Callable<JsonNode> withEstimatedGas(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethEstimateGas(mapper.convertValue(saplObject.get(TRANSACTION),
				org.web3j.protocol.core.methods.request.Transaction.class)).send().getAmountUsed());

	}

	@Attribute(name = "blockByHash", docs = "Returns information about a block by hash.")
	public Flux<JsonNode> ethGetBlockByHash(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withBlockByHash(saplObject));

	}

	private Callable<JsonNode> withBlockByHash(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetBlockByHash(getStringFrom(saplObject, BLOCK_HASH),
				saplObject.get(RETURN_FULL_TRANSACTION_OBJECTS).asBoolean(false)).send().getBlock());

	}

	@Attribute(name = "blockByNumber", docs = "Returns information about a block by block number.")
	public Flux<JsonNode> ethGetBlockByNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withBlockByNumber(saplObject));

	}

	private Callable<JsonNode> withBlockByNumber(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetBlockByNumber(extractDefaultBlockParameter(saplObject),
				saplObject.get(RETURN_FULL_TRANSACTION_OBJECTS).asBoolean(false)).send().getBlock());

	}

	@Attribute(name = "transactionByHash", docs = "Returns the information about a transaction requested by transaction hash.")
	public Flux<JsonNode> ethGetTransactionByHash(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withTransactionByHash(saplObject));

	}

	private Callable<JsonNode> withTransactionByHash(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetTransactionByHash(saplObject.textValue()).send().getTransaction());

	}

	@Attribute(name = "transactionByBlockHashAndIndex", docs = "Returns information about a transaction by block hash and transaction index position.")
	public Flux<JsonNode> ethGetTransactionByBlockHashAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withTransactionByBlockHashAndIndex(saplObject));

	}

	private Callable<JsonNode> withTransactionByBlockHashAndIndex(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetTransactionByBlockHashAndIndex(getStringFrom(saplObject, BLOCK_HASH),
				getBigIntFrom(saplObject, TRANSACTION_INDEX)).send().getTransaction());

	}

	@Attribute(name = "transactionByBlockNumberAndIndex", docs = "Returns information about a transaction by block number and transaction index position.")
	public Flux<JsonNode> ethGetTransactionByBlockNumberAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withTransactionByBlockNumberAndIndex(saplObject));

	}

	private Callable<JsonNode> withTransactionByBlockNumberAndIndex(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetTransactionByBlockNumberAndIndex(extractDefaultBlockParameter(saplObject),
						getBigIntFrom(saplObject, TRANSACTION_INDEX)).send().getTransaction());

	}

	@Attribute(name = "transactionReceipt", docs = "Returns the receipt of a transaction by transaction hash.")
	public Flux<JsonNode> ethGetTransactionReceipt(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withTransactionReceipt(saplObject));

	}

	private Callable<JsonNode> withTransactionReceipt(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetTransactionReceipt(saplObject.textValue()).send().getTransactionReceipt());

	}

	@Attribute(name = "pendingTransactions", docs = "Returns the pending transactions list.")
	public Flux<JsonNode> ethPendingTransactions(JsonNode saplObject, Map<String, JsonNode> variables) {

		return Flux.from(web3j.ethPendingTransactionHashFlowable().cast(JsonNode.class));

	}

	@Attribute(name = "uncleByBlockHashAndIndex", docs = "Returns information about a uncle of a block by hash and uncle index position.")
	public Flux<JsonNode> ethGetUncleByBlockHashAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withUncleByBlockHashAndIndex(saplObject));

	}

	private Callable<JsonNode> withUncleByBlockHashAndIndex(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetUncleByBlockHashAndIndex(getStringFrom(saplObject, BLOCK_HASH),
				getBigIntFrom(saplObject, TRANSACTION_INDEX)).send().getBlock());

	}

	@Attribute(name = "uncleByBlockNumberAndIndex", docs = "Returns information about a uncle of a block by number and uncle index position.")
	public Flux<JsonNode> ethGetUncleByBlockNumberAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withUncleByBlockNumberAndIndex(saplObject));

	}

	private Callable<JsonNode> withUncleByBlockNumberAndIndex(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetUncleByBlockNumberAndIndex(extractDefaultBlockParameter(saplObject),
				getBigIntFrom(saplObject, TRANSACTION_INDEX)).send().getBlock());

	}

	@Attribute(name = "ethFilterChanges", docs = "Polling method for a filter, which returns an array of logs which occurred since last poll.")
	public Flux<JsonNode> ethGetFilterChanges(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withFilterChanges(saplObject));

	}

	private Callable<JsonNode> withFilterChanges(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetFilterChanges(getBigIntFrom(saplObject, FILTER_ID)).send().getLogs());

	}

	@Attribute(name = "ethFilterLogs", docs = "Returns an array of all logs matching filter with given id.")
	public Flux<JsonNode> ethGetFilterLogs(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withFilterLogs(saplObject));

	}

	private Callable<JsonNode> withFilterLogs(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.ethGetFilterLogs(getBigIntFrom(saplObject, FILTER_ID)).send().getLogs());

	}

	@Attribute(name = "logs", docs = "Returns an array of all logs matching a given filter object.")
	public Flux<JsonNode> ethGetLogs(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withLogs(saplObject));

	}

	private Callable<JsonNode> withLogs(JsonNode saplObject) {

		return () -> convertToJsonNode(
				web3j.ethGetLogs(mapper.convertValue(saplObject, EthFilter.class)).send().getLogs());

	}

	@Attribute(name = "work", docs = "Returns the hash of the current block, the seedHash, and the boundary condition to be met (\"target\").")
	public Flux<JsonNode> ethGetWork(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withWork());

	}

	private Callable<JsonNode> withWork() {

		return () -> convertToJsonNode(web3j.ethGetWork().send().getResult());

	}

	@Attribute(name = "shhVersion", docs = "Returns the current whisper protocol version.")
	public Flux<JsonNode> shhVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withShhVersion());

	}

	private Callable<JsonNode> withShhVersion() {

		return () -> convertToJsonNode(web3j.shhVersion().send().getVersion());

	}

	@Attribute(name = "hasIdentity", docs = "Checks if the client hold the private keys for a given identity.")
	public Flux<JsonNode> shhHasIdentity(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withHasIdentity(saplObject));

	}

	private Callable<JsonNode> withHasIdentity(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.shhHasIdentity(saplObject.textValue()).send().getResult());

	}

	@Attribute(name = "shhFilterChanges", docs = "Polling method for whisper filters. Returns new messages since the last call of this method.")
	public Flux<JsonNode> shhGetFilterChanges(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withShhFilterChanges(saplObject));

	}

	private Callable<JsonNode> withShhFilterChanges(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.shhGetFilterChanges(saplObject.bigIntegerValue()).send().getMessages());

	}

	@Attribute(name = "messages", docs = "Get all messages matching a filter. Unlike shh_getFilterChanges this returns all messages.")
	public Flux<JsonNode> shhGetMessages(JsonNode saplObject, Map<String, JsonNode> variables) {
		return scheduledFlux(withShhMessages(saplObject));

	}

	private Callable<JsonNode> withShhMessages(JsonNode saplObject) {

		return () -> convertToJsonNode(web3j.shhGetMessages(saplObject.bigIntegerValue()).send().getMessages());

	}

	private Flux<JsonNode> scheduledFlux(Callable<JsonNode> functionToCall) {
		Flux<JsonNode> returnFlux = timer.map((i) -> {
			try {
				return functionToCall.call();
			}
			catch (Exception e) {
				LOGGER.warn(
						"An error occurred, so by default a Json NullNode is returned. The following exception has been thrown:\n"
								+ e);
			}
			return JSON.nullNode();
		});

		return returnFlux;

	}

	private static JsonNode convertToJsonNode(Object o) {
		return mapper.convertValue(o, JsonNode.class);
	}

	private static String getStringFrom(JsonNode saplObject, String stringName) {
		if (saplObject.has(stringName)) {
			return saplObject.get(stringName).textValue();
		}
		LOGGER.warn("The input JsonNode for the policy didn't contain a field of type " + stringName
				+ ", altough this was expected. Ignore this message if the field was optional.");
		return null;
	}

	private static BigInteger getBigIntFrom(JsonNode saplObject, String bigIntegerName) {
		if (saplObject.has(bigIntegerName)) {
			return saplObject.get(bigIntegerName).bigIntegerValue();
		}
		LOGGER.warn("The input JsonNode for the policy didn't contain a field of type " + bigIntegerName
				+ ", altough this was expected. Ignore this message if the field was optional.");
		return null;
	}

}
