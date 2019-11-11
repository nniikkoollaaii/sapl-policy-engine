package io.sapl.interpreter.pip;

import static io.sapl.interpreter.pip.EthereumPipFunctions.convertToType;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.ShhFilter;
import org.web3j.protocol.core.methods.request.ShhPost;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.AttributeException;
import io.sapl.api.pip.PolicyInformationPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * The Ethereum Policy Information Point gives access to most methods of the
 * JSON-RPC Ethereum API (https://github.com/ethereum/wiki/wiki/JSON-RPC)
 *
 * Excluded are the deprecated methods eth_getCompilers, eth_compileSolidity,
 * eth_compileLLL and eth_compileSerpent. Further excluded are all db_ methods
 * as they are deprecated and will be removed. Also excluded is the eth_getProof
 * method as at time of writing this there doesn't exist an implementation in
 * the Web3j API.
 *
 * Furthermore the methods verifyTransaction and loadContractInformation are not
 * part of the JSON RPC API but are considered to be a more user friendly
 * implementation of the most common use cases.
 */

@Slf4j
@PolicyInformationPoint(name = "ethereum", description = "Connects to the Ethereum Blockchain.")
public class EthereumPolicyInformationPoint {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EthereumPolicyInformationPoint.class);

    private Web3j web3j;
    
    
    private static final String ADDRESS = "address";
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
    private static final String NONCE = "nonce";
    private static final String HEADER_POW_HASH = "headerPowHash";
    private static final String MIX_DIGEST = "mixDigest";
    private static final String HASHRATE = "hashrate";
    private static final String CLIENT_ID = "clientId";
    private static final String DEFAULT_BLOCK_PARAMETER_BIG_INT = "defaultBlockParameterBigInt";
    private static final String DEFAULT_BLOCK_PARAMETER_STRING = "defaultBlockParameterString";
    private static final String LATEST = "latest";
    private static final String EARLIEST = "earliest";
    private static final String PENDING = "pending";
    private static final String NO_DBP_WARNING = "The DefaultBlockParameter was not correctly provided. By default the latest Block is used.";
    private static final String VERIFY_TRANSACTION_WARNING = "There was an error during verifyTransaction. By default false is returned but the transaction could have taken place.";

    public EthereumPolicyInformationPoint(Web3jService web3jService) {
	web3j = Web3j.build(web3jService);
    }

    /**
     * Method for verifying if a given transaction has taken place.
     *
     * @param saplObject needs to have the following values: <br>
     *                   "transactionHash" : The hash of the transaction that should
     *                   be verified <br>
     *                   "fromAccount" : The adress of the account the transaction
     *                   is send from <br>
     *                   "toAccount" : The adress of the account that receives the
     *                   transaction <br>
     *                   "transactionValue" : A BigInteger that represents the value
     *                   of the transaction in Wei
     *
     * @param variables  is unused here
     * @return A single JsonNode that has boolean value true if the transaction has
     *         taken place and false otherwise
     * @throws AttributeException
     */
    @Attribute(name = "transaction", docs = "Returns true, if a transaction has taken place and false otherwise.")
    public Flux<JsonNode> verifyTransaction(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {

	    EthTransaction ethTransaction = web3j.ethGetTransactionByHash(saplObject.get(TRANSACTION_HASH).textValue())
		    .send();
	    Optional<Transaction> optionalTransactionFromChain = ethTransaction.getTransaction();
	    if (optionalTransactionFromChain.isPresent()) {
		Transaction transactionFromChain = optionalTransactionFromChain.get();
		if (transactionFromChain.getFrom().equals(saplObject.get(FROM_ACCOUNT).textValue())
			&& transactionFromChain.getTo().equals(saplObject.get(TO_ACCOUNT).textValue())
			&& transactionFromChain.getValue()
				.equals(saplObject.get(TRANSACTION_VALUE).bigIntegerValue())) {
		    return convertToFlux(true);
		}
	    }
	} catch (IOException | NullPointerException e) {
	    logger.warn(VERIFY_TRANSACTION_WARNING);
	}

	return convertToFlux(false);
    }

    /**
     * Method for querying the state of a contract.
     *
     * @param saplObject needs to have the following values <br>
     *                   "fromAccount" : The account that makes the request <br>
     *                   "toAccount" : The address of the called contract <br>
     *                   "functionName" : The name of the called function. <br>
     *                   "inputParams" : A Json ArrayNode that contains a tuple of
     *                   "type" and "value" for each input parameter. Example:
     *                   [{"type" : "uint32", "value" : 45},{"type" : "bool",
     *                   "value" : "true"}] <br>
     *                   "outputParams" : A Json ArrayNode that contains the return
     *                   types. Example: ["address","bool"] <br>
     *                   All types that can be used are listed in the getType-method
     *                   of the <a href=
     *                   "https://github.com/web3j/web3j/blob/master/abi/src/main/java/org/web3j/abi/datatypes/AbiTypes.java">AbiTypes</a>.
     * @param variables  is unused here
     * @return The return value(s) of the called contract function as an Array Node.
     *         Each node entry contains two values, "value" with the return value
     *         and "typeAsString" with the return type. Example for a node entry:
     *         {"value":true,"typeAsString":"bool"}
     * @throws AttributeException
     */
    @Attribute(name = "contract", docs = "Returns the result of a function call of a specified contract.")
    public Flux<JsonNode> loadContractInformation(JsonNode saplObject, Map<String, JsonNode> variables)
	    throws AttributeException {
	try {
	    String from = getStringFrom(saplObject, FROM_ACCOUNT);
	    String to = getStringFrom(saplObject, TO_ACCOUNT);

	    List<Type> inputParameters = new ArrayList<>();
	    JsonNode inputNode = saplObject.get(INPUT_PARAMS);
	    if (inputNode.isArray()) {
		for (JsonNode inputParam : inputNode) {
		    inputParameters.add(convertToType(inputParam));
		}
	    }

	    List<TypeReference<?>> outputParameters = new ArrayList<>();
	    JsonNode outputNode = saplObject.get(OUTPUT_PARAMS);
	    if (outputNode.isArray()) {
		for (JsonNode solidityType : outputNode) {
		    outputParameters.add(TypeReference.makeTypeReference(solidityType.textValue()));
		}
	    }
	    Function function = new Function(getStringFrom(saplObject, FUNCTION_NAME), inputParameters,
		    outputParameters);

	    String encodedFunction = FunctionEncoder.encode(function);
	    org.web3j.protocol.core.methods.response.EthCall response = web3j
		    .ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(from, to,
			    encodedFunction), extractDefaultBlockParameter(saplObject))
		    .send();

	    List<Type> output = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

	    return convertToFlux(output);

	} catch (IOException | ClassNotFoundException e) {
	    throw new AttributeException(e);
	}
    }

    @Attribute(name = "web3_clientVersion", docs = "Returns the current client version.")
    public Flux<JsonNode> web3ClientVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.web3ClientVersion().send().getWeb3ClientVersion());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "web3_sha3", docs = "Returns Keccak-256 (not the standardized SHA3-256) of the given data.")
    public Flux<JsonNode> web3Sha3(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.web3Sha3(saplObject.textValue()).send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "net_version", docs = "Returns the current network id.")
    public Flux<JsonNode> netVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.netVersion().send().getNetVersion());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "net_listening", docs = "Returns true if client is actively listening for network connections.")
    public Flux<JsonNode> netListening(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.netListening().send().isListening());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "net_peerCount", docs = "Returns number of peers currently connected to the client.")
    public Flux<JsonNode> netPeerCount(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.netPeerCount().send().getQuantity());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_protocolVersion", docs = "Returns the current ethereum protocol version.")
    public Flux<JsonNode> ethProtocolVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethProtocolVersion().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_syncing", docs = "Returns an object with data about the sync status or false.")
    public Flux<JsonNode> ethSyncing(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethSyncing().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_coinbase", docs = "Returns the client coinbase address.")
    public Flux<JsonNode> ethCoinbase(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethCoinbase().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_mining", docs = "Returns true if client is actively mining new blocks.")
    public Flux<JsonNode> ethMining(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethMining().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_hashrate", docs = "Returns the number of hashes per second that the node is mining with.")
    public Flux<JsonNode> ethHashrate(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethHashrate().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_gasPrice", docs = "Returns the current price per gas in wei.")
    public Flux<JsonNode> ethGasPrice(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGasPrice().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_accounts", docs = "Returns a list of addresses owned by client.")
    public Flux<JsonNode> ethAccounts(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethAccounts().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_blockNumber", docs = "Returns the number of most recent block.")
    public Flux<JsonNode> ethBlockNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethBlockNumber().send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_getBalance", docs = "Returns the balance of the account of given address.")
    public Flux<JsonNode> ethGetBalance(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(
		    web3j.ethGetBalance(saplObject.get(ADDRESS).textValue(), extractDefaultBlockParameter(saplObject))
			    .send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_getStorageAt", docs = "Returns the value from a storage position at a given address.")
    public Flux<JsonNode> ethGetStorageAt(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j
		    .ethGetStorageAt(saplObject.get(ADDRESS).textValue(),
			    saplObject.get(POSITION).bigIntegerValue(), extractDefaultBlockParameter(saplObject))
		    .send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getTransactionCount", docs = "Returns the number of transactions sent from an address.")
    public Flux<JsonNode> ethGetTransactionCount(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetTransactionCount(saplObject.get(ADDRESS).textValue(),
		    extractDefaultBlockParameter(saplObject)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getBlockTransactionCountByHash", docs = "Returns the number of transactions in a block from a block matching the given block hash.")
    public Flux<JsonNode> ethGetBlockTransactionCountByHash(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(
		    web3j.ethGetBlockTransactionCountByHash(saplObject.get(BLOCK_HASH).textValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getBlockTransactionCountByNumber", docs = "Returns the number of transactions in a block matching the given block number.")
    public Flux<JsonNode> ethGetBlockTransactionCountByNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(
		    web3j.ethGetBlockTransactionCountByNumber(extractDefaultBlockParameter(saplObject)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getUncleCountByBlockHash", docs = "Returns the number of uncles in a block from a block matching the given block hash.")
    public Flux<JsonNode> ethGetUncleCountByBlockHash(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetUncleCountByBlockHash(saplObject.get(BLOCK_HASH).textValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getUncleCountByBlockNumber", docs = "Returns the number of uncles in a block from a block matching the given block number.")
    public Flux<JsonNode> ethGetUncleCountByBlockNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetUncleCountByBlockNumber(extractDefaultBlockParameter(saplObject)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getCode", docs = "Returns code at a given address.")
    public Flux<JsonNode> ethGetCode(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(
		    web3j.ethGetCode(saplObject.get(ADDRESS).textValue(), extractDefaultBlockParameter(saplObject))
			    .send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_sign", docs = "The sign method calculates an Ethereum specific signature.")
    public Flux<JsonNode> ethSign(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j
		    .ethSign(saplObject.get(ADDRESS).textValue(), saplObject.get(SHA3_HASH_OF_DATA_TO_SIGN).textValue())
		    .send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_sendTransaction", docs = "Creates new message call transaction or a contract creation, if the data field contains code.")
    public Flux<JsonNode> ethSendTransaction(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j
		    .ethSendTransaction(
			    mapper.convertValue(saplObject, org.web3j.protocol.core.methods.request.Transaction.class))
		    .send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_sendRawTransaction", docs = "Creates new message call transaction or a contract creation for signed transactions.")
    public Flux<JsonNode> ethSendRawTransaction(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethSendRawTransaction(saplObject.textValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_call", docs = "Executes a new message call immediately without creating a transaction on the block chain.")
    public Flux<JsonNode> ethCall(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethCall(
		    mapper.convertValue(saplObject.get(TRANSACTION),
			    org.web3j.protocol.core.methods.request.Transaction.class),
		    extractDefaultBlockParameter(saplObject)).send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_estimateGas", docs = "Generates and returns an estimate of how much gas is necessary to allow the transaction to complete.")
    public Flux<JsonNode> ethEstimateGas(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethEstimateGas(mapper.convertValue(saplObject.get(TRANSACTION),
		    org.web3j.protocol.core.methods.request.Transaction.class)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getBlockByHash", docs = "Returns information about a block by hash.")
    public Flux<JsonNode> ethGetBlockByHash(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetBlockByHash(saplObject.get(BLOCK_HASH).textValue(),
		    saplObject.get(RETURN_FULL_TRANSACTION_OBJECTS).asBoolean(false)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getBlockByNumber", docs = "Returns information about a block by block number.")
    public Flux<JsonNode> ethGetBlockByNumber(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetBlockByNumber(extractDefaultBlockParameter(saplObject),
		    saplObject.get(RETURN_FULL_TRANSACTION_OBJECTS).asBoolean(false)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getTransactionByHash", docs = "Returns the information about a transaction requested by transaction hash.")
    public Flux<JsonNode> ethGetTransactionByHash(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetTransactionByHash(saplObject.textValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getTransactionByBlockHashAndIndex", docs = "Returns information about a transaction by block hash and transaction index position.")
    public Flux<JsonNode> ethGetTransactionByBlockHashAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetTransactionByBlockHashAndIndex(saplObject.get(BLOCK_HASH).textValue(),
		    getBigIntFrom(saplObject, TRANSACTION_INDEX)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getTransactionByBlockNumberAndIndex", docs = "Returns information about a transaction by block number and transaction index position.")
    public Flux<JsonNode> ethGetTransactionByBlockNumberAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetTransactionByBlockNumberAndIndex(extractDefaultBlockParameter(saplObject),
		    getBigIntFrom(saplObject, TRANSACTION_INDEX)).send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_getTransactionReceipt", docs = "Returns the receipt of a transaction by transaction hash.")
    public Flux<JsonNode> ethGetTransactionReceipt(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetTransactionReceipt(saplObject.textValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_pendingTransactions", docs = "Returns the pending transactions list.")
    public Flux<JsonNode> ethPendingTransactions(JsonNode saplObject, Map<String, JsonNode> variables) {

	return Flux.from(web3j.ethPendingTransactionHashFlowable().cast(JsonNode.class));

    }

    @Attribute(name = "eth_getUncleByBlockHashAndIndex", docs = "Returns information about a uncle of a block by hash and uncle index position.")
    public Flux<JsonNode> ethGetUncleByBlockHashAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetUncleByBlockHashAndIndex(saplObject.get(BLOCK_HASH).textValue(),
		    getBigIntFrom(saplObject, TRANSACTION_INDEX)).send());
	} catch (IOException e) {

	}
	return convertToFlux(null);
    }

    @Attribute(name = "eth_getUncleByBlockNumberAndIndex", docs = "Returns information about a uncle of a block by number and uncle index position.")
    public Flux<JsonNode> ethGetUncleByBlockNumberAndIndex(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetUncleByBlockNumberAndIndex(extractDefaultBlockParameter(saplObject),
		    getBigIntFrom(saplObject, TRANSACTION_INDEX)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_newBlockFilter", docs = "Creates a filter in the node, to notify when a new block arrives. To check if the state has changed, call eth_getFilterChanges.")
    public Flux<JsonNode> ethNewBlockFilter(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethNewBlockFilter().send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_newPendingTransactionFilter", docs = "Creates a filter in the node, to notify when new pending transactions arrive. To check if the state has changed, call eth_getFilterChanges.")
    public Flux<JsonNode> ethNewPendingTransactionFilter(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethNewPendingTransactionFilter().send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_uninstallFilter", docs = "Uninstalls a filter with given id. Should always be called when watch is no longer needed. Additonally Filters timeout when they aren't requested with eth_getFilterChanges for a period of time.")
    public Flux<JsonNode> ethUninstallFilter(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethUninstallFilter(getBigIntFrom(saplObject, FILTER_ID)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getFilterChanges", docs = "Polling method for a filter, which returns an array of logs which occurred since last poll.")
    public Flux<JsonNode> ethGetFilterChanges(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetFilterChanges(getBigIntFrom(saplObject, FILTER_ID)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getFilterLogs", docs = "Returns an array of all logs matching filter with given id.")
    public Flux<JsonNode> ethGetFilterLogs(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetFilterLogs(getBigIntFrom(saplObject, FILTER_ID)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getLogs", docs = "Returns an array of all logs matching a given filter object.")
    public Flux<JsonNode> ethGetLogs(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetLogs(mapper.convertValue(saplObject, EthFilter.class)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_getWork", docs = "Returns the hash of the current block, the seedHash, and the boundary condition to be met (\"target\").")
    public Flux<JsonNode> ethGetWork(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethGetWork().send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_submitWork", docs = "Used for submitting a proof-of-work solution.")
    public Flux<JsonNode> ethSubmitWork(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.ethSubmitWork(getStringFrom(saplObject, NONCE),
		    getStringFrom(saplObject, HEADER_POW_HASH), getStringFrom(saplObject, MIX_DIGEST)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "eth_submitHashrate", docs = "Used for submitting mining hashrate.")
    public Flux<JsonNode> ethSubmitHashrate(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j
		    .ethSubmitHashrate(getStringFrom(saplObject, HASHRATE), getStringFrom(saplObject, CLIENT_ID))
		    .send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    /**
     * Method was not implemented because it is not available in the Web3j API.
     */

    @Attribute(name = "eth_getProof", docs = "Returns the account- and storage-values of the specified account including the Merkle-proof.")
    public Flux<JsonNode> ethGetProof(JsonNode saplObject, Map<String, JsonNode> variables) {

	return convertToFlux(null);
    }

    @Attribute(name = "shh_version", docs = "Returns the current whisper protocol version.")
    public Flux<JsonNode> shhVersion(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhVersion().send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_post", docs = "Sends a whisper message.")
    public Flux<JsonNode> shhPost(JsonNode saplObject, Map<String, JsonNode> variables) {

	try {
	    return convertToFlux(web3j.shhPost(mapper.convertValue(saplObject, ShhPost.class)).send());
	} catch (IllegalArgumentException | IOException e) {

	}

	return convertToFlux(null);

    }

    @Attribute(name = "shh_newIdentity", docs = "Creates new whisper identity in the client.")
    public Flux<JsonNode> shhNewIdentity(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhNewIdentity().send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_hasIdentity", docs = "Checks if the client hold the private keys for a given identity.")
    public Flux<JsonNode> shhHasIdentity(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhHasIdentity(saplObject.textValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_newGroup", docs = "Creates a new group.")
    public Flux<JsonNode> shhNewGroup(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhNewGroup().send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_addToGroup", docs = "Adds a whisper identity to the group.")
    public Flux<JsonNode> shhAddToGroup(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhAddToGroup(saplObject.textValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_newFilter", docs = "Creates filter to notify, when client receives whisper message matching the filter options.")
    public Flux<JsonNode> shhNewFilter(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhNewFilter(mapper.convertValue(saplObject, ShhFilter.class)).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_uninstallFilter", docs = "Uninstalls a filter with given id. Should always be called when watch is no longer needed. Additonally Filters timeout when they aren't requested with shh_getFilterChanges for a period of time.")
    public Flux<JsonNode> shhUninstallFilter(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhUninstallFilter(saplObject.bigIntegerValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_getFilterChanges", docs = "Polling method for whisper filters. Returns new messages since the last call of this method.")
    public Flux<JsonNode> shhGetFilterChanges(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhGetFilterChanges(saplObject.bigIntegerValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    @Attribute(name = "shh_getMessages", docs = "Get all messages matching a filter. Unlike shh_getFilterChanges this returns all messages.")
    public Flux<JsonNode> shhGetMessages(JsonNode saplObject, Map<String, JsonNode> variables) {
	try {
	    return convertToFlux(web3j.shhGetMessages(saplObject.bigIntegerValue()).send());
	} catch (IOException e) {

	}

	return convertToFlux(null);
    }

    private static Flux<JsonNode> convertToFlux(Object o) {
	return Flux.just(mapper.convertValue(o, JsonNode.class));
    }

    private static String getStringFrom(JsonNode saplObject, String stringName) {
	return saplObject.get(stringName).textValue();
    }

    private static BigInteger getBigIntFrom(JsonNode saplObject, String bigIntegerName) {
	return saplObject.get(bigIntegerName).bigIntegerValue();
    }

    /**
     * Determines the DefaultBlockParameter needed for some Ethereum API calls. This
     * Parameter can be a BigInteger number or one of the Strings "latest",
     * "earliest" or "pending". If the DefaultBlockParameter is not provided in the
     * policy, the latest Block is used. In this case there is also a warning.
     *
     * Please use the following names in your SAPL Object: The above mentioned value
     * of DBPBI if you want to use a BigInteger. The above mentioned value of DBPS
     * if you want to use a String.
     *
     * @param saplObject
     * @return
     */
    private static DefaultBlockParameter extractDefaultBlockParameter(JsonNode saplObject) {
	if (saplObject.has(DEFAULT_BLOCK_PARAMETER_BIG_INT)) {
	    JsonNode dbp = saplObject.get(DEFAULT_BLOCK_PARAMETER_BIG_INT);
	    BigInteger dbpValue = dbp.bigIntegerValue();
	    return DefaultBlockParameter.valueOf(dbpValue);
	}
	if (saplObject.has(DEFAULT_BLOCK_PARAMETER_STRING)) {
	    String dbpsName = saplObject.get(DEFAULT_BLOCK_PARAMETER_STRING).textValue();
	    if (dbpsName.equals(EARLIEST) || dbpsName.equals(LATEST) || dbpsName.equals(PENDING))
		return DefaultBlockParameter.valueOf(dbpsName);
	}

	LOGGER.warn(NO_DBP_WARNING);
	return DefaultBlockParameter.valueOf(LATEST);

    }

}
