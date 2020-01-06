package io.sapl.interpreter.pip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Async;
import org.web3j.utils.Convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pip.AttributeException;
import io.sapl.interpreter.pip.contracts.Authorization;
import io.sapl.interpreter.pip.contracts.DeviceOperatorCertificate;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Testing the EthereumPIP with an ethereum blockchain node running in a Docker container. The node is the hyperledger
 * besu implemenation (https://besu.hyperledger.org/en/stable/)
 *
 * This test requires running Docker with version at least 1.6.0 and Docker environment should have more than 2GB free
 * disk space.
 *
 * The accounts used for testing are publicly known from the besu website
 * (https://besu.hyperledger.org/en/stable/Reference/Accounts-for-Testing/) <br>
 * DO NOT USE THESES ACCOUNTS IN THE MAIN NET. ANY ETHER SENT TO THESE ACCOUNTS WILL BE LOST.
 *
 */
@SuppressWarnings("rawtypes")
public class EthereumIntegrationTest {

	private static final String HTTP_LOCALHOST = "http://localhost:";

	private static final String CERTIFICATION = "certification";

	private static final String HAS_CERTIFICATE = "hasCertificate";

	private static final String WRONG_NAME = "wrongName";

	private static final String ACCESS = "access";

	private static final String ETHEREUM = "ethereum";

	private static final String OUTPUT_PARAMS = "outputParams";

	private static final String BOOL = "bool";

	private static final String INPUT_PARAMS = "inputParams";

	private static final String VALUE = "value";

	private static final String ADDRESS = "address";

	private static final String TYPE = "type";

	private static final String TO_ACCOUNT = "toAccount";

	private static final String FROM_ACCOUNT = "fromAccount";

	private static final String CONTRACT_ADDRESS = "contractAddress";

	private static final String TRANSACTION_HASH = "transactionHash";

	private static final String TRANSACTION_VALUE = "transactionValue";

	private static final String FUNCTION_NAME = "functionName";

	private static final String IS_AUTHORIZED = "isAuthorized";

	private static final String USER1_ADDRESS = "0xfe3b557e8fb62b89f4916b721be55ceb828dbd73";

	private static final String USER1_PRIVATE_KEY = "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63";

	private static final String USER2_ADDRESS = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57";

	private static final String USER2_PRIVATE_KEY = "0xc87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3";

	private static final String USER3_ADDRESS = "0xf17f52151EbEF6C7334FAD080c5704D77216b732";

	private static final String USER3_PRIVATE_KEY = "0xae6ae8e5ccbfb04590405997ee2d52d2b330726137b875053c36d94e974d162f";

	private static final BigInteger TRANSACTION1_VALUE = new BigInteger("2000000000000000000");

	private static final String DEFAULT_BLOCK_PARAMETER_STRING = "defaultBlockParameterString";

	private static final String DEFAULT_BLOCK_PARAMETER_BIG_INT = "defaultBlockParameterBigInt";

	private static final String LATEST = "latest";

	private static final String POSITION = "position";

	private static final String BLOCK_HASH = "blockHash";

	private static Web3j web3j;

	private static EthereumPolicyInformationPoint ethPip;

	private static EmbeddedPolicyDecisionPoint pdp;

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static String authContractAddress;

	private static String certContractAddress;

	private static TransactionReceipt transactionReceiptUser2;

	private static TransactionReceipt transactionReceiptUser3;

	@ClassRule
	public static final GenericContainer besuContainer = new GenericContainer("hyperledger/besu:latest")
			.withExposedPorts(8545, 8546)
			.withCommand("--miner-enabled", "--miner-coinbase=" + USER1_ADDRESS, "--rpc-http-enabled", "--network=dev")
			.waitingFor(Wait.forHttp("/liveness").forStatusCode(200).forPort(8545));

	@BeforeClass
	public static void init() throws InterruptedException, TransactionException, Exception {
		final Integer port = besuContainer.getMappedPort(8545);
		web3j = Web3j.build(new HttpService(HTTP_LOCALHOST + port), 500, Async.defaultExecutorService());
		ethPip = new EthereumPolicyInformationPoint(new HttpService(HTTP_LOCALHOST + port));

		String path = "src/test/resources";
		File file = new File(path);
		String absolutePath = file.getAbsolutePath();

		pdp = EmbeddedPolicyDecisionPoint.builder().withFilesystemPDPConfigurationProvider(absolutePath + "/policies")
				.withFilesystemPolicyRetrievalPoint(absolutePath + "/policies", IndexType.SIMPLE)
				.withPolicyInformationPoint(ethPip).build();

		Credentials credentials = Credentials.create(USER1_PRIVATE_KEY);
		transactionReceiptUser2 = Transfer.sendFunds(web3j, credentials, USER2_ADDRESS,
				BigDecimal.valueOf(TRANSACTION1_VALUE.doubleValue()), Convert.Unit.WEI).send();
		transactionReceiptUser3 = Transfer
				.sendFunds(web3j, credentials, USER3_ADDRESS, BigDecimal.valueOf(3.3), Convert.Unit.ETHER).send();

		Authorization authContract = Authorization.deploy(web3j, credentials, new DefaultGasProvider()).send();
		authContractAddress = authContract.getContractAddress();
		authContract.authorize(USER2_ADDRESS).send();

		DeviceOperatorCertificate certContract = DeviceOperatorCertificate
				.deploy(web3j, credentials, new DefaultGasProvider()).send();
		certContractAddress = certContract.getContractAddress();
		certContract.addIssuer(USER1_ADDRESS);
		certContract.issueCertificate(USER2_ADDRESS);

	}

	// Test with Policy

	@Test
	@Ignore
	public void loadContractInformationShouldWorkInCertificatePolicy() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(CONTRACT_ADDRESS, certContractAddress);
		saplObject.put(FUNCTION_NAME, HAS_CERTIFICATE);

		ArrayNode inputParams = JSON.arrayNode();
		ObjectNode input1 = JSON.objectNode();
		input1.put(TYPE, ADDRESS);
		input1.put(VALUE, USER2_ADDRESS.substring(2));
		inputParams.add(input1);
		saplObject.set(INPUT_PARAMS, inputParams);

		ArrayNode outputParams = JSON.arrayNode();
		outputParams.add(BOOL);
		saplObject.set(OUTPUT_PARAMS, outputParams);

		AuthorizationSubscription authzSubscription = new AuthorizationSubscription(saplObject, JSON.textNode(ACCESS),
				JSON.textNode(CERTIFICATION), null);
		final Flux<AuthorizationDecision> decision = pdp.decide(authzSubscription);
		StepVerifier.create(decision).expectNextMatches(authzDecision -> authzDecision.getDecision() == Decision.PERMIT)
				.thenCancel().verify();

	}

	@Test
	public void loadContractInformationShouldWorkInAuthorizationPolicy() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(CONTRACT_ADDRESS, authContractAddress);
		saplObject.put(FUNCTION_NAME, IS_AUTHORIZED);
		ArrayNode inputParams = JSON.arrayNode();
		ObjectNode input1 = JSON.objectNode();
		input1.put(TYPE, ADDRESS);
		input1.put(VALUE, USER2_ADDRESS.substring(2));
		inputParams.add(input1);
		saplObject.set(INPUT_PARAMS, inputParams);
		ArrayNode outputParams = JSON.arrayNode();
		outputParams.add(BOOL);
		saplObject.set(OUTPUT_PARAMS, outputParams);
		AuthorizationSubscription authzSubscription = new AuthorizationSubscription(saplObject, JSON.textNode(ACCESS),
				JSON.textNode(ETHEREUM), null);
		final Flux<AuthorizationDecision> decision = pdp.decide(authzSubscription);
		StepVerifier.create(decision).expectNextMatches(authzDecision -> authzDecision.getDecision() == Decision.PERMIT)
				.thenCancel().verify();
	}

	// Timed Testing

	// loadContractInformation

	@Test
	public void loadContractInformationWithAuthorizationShouldReturnCorrectValue() throws AttributeException {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(CONTRACT_ADDRESS, authContractAddress);
		saplObject.put(FUNCTION_NAME, IS_AUTHORIZED);
		ArrayNode inputParams = JSON.arrayNode();
		ObjectNode input1 = JSON.objectNode();
		input1.put(TYPE, ADDRESS);
		input1.put(VALUE, USER2_ADDRESS.substring(2));
		inputParams.add(input1);
		saplObject.set(INPUT_PARAMS, inputParams);
		ArrayNode outputParams = JSON.arrayNode();
		outputParams.add(BOOL);
		saplObject.set(OUTPUT_PARAMS, outputParams);
		JsonNode result = ethPip.loadContractInformation(saplObject, null).blockFirst();

		assertTrue("False was returned although user2 was authorized and result should have been true.",
				result.get(0).get(VALUE).asBoolean());

	}

	@Test
	@Ignore
	public void loadContractInformationShouldWorkWithCertificateContract() throws AttributeException {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(CONTRACT_ADDRESS, certContractAddress);
		saplObject.put(FUNCTION_NAME, HAS_CERTIFICATE);

		ArrayNode inputParams = JSON.arrayNode();
		ObjectNode input1 = JSON.objectNode();
		input1.put(TYPE, ADDRESS);
		input1.put(VALUE, USER2_ADDRESS.substring(2));
		inputParams.add(input1);
		saplObject.set(INPUT_PARAMS, inputParams);

		ArrayNode outputParams = JSON.arrayNode();
		outputParams.add(BOOL);
		saplObject.set(OUTPUT_PARAMS, outputParams);
		JsonNode result = ethPip.loadContractInformation(saplObject, null).blockFirst();

		assertTrue("False was returned although user2 was certified and result should have been true.",
				result.get(0).get(VALUE).asBoolean());
	}

	// verifyTransaction

	@Test
	public void verifyTransactionShouldReturnTrueWithCorrectTransaction() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser2.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER1_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER2_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, TRANSACTION1_VALUE);
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertTrue("Transaction was not validated as true although it is correct.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseValue() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser2.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER1_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER2_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("25"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the value was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseSender() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser2.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER3_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER2_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("2000000000000000000"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the sender was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseRecipient() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser2.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER1_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER3_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("2000000000000000000"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the recipient was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseTransactionHash() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser3.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER1_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER2_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("2000000000000000000"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the TransactionHash was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithNullInput() {
		boolean result = ethPip.verifyTransaction(null, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the input was null.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithWrongInput1() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(WRONG_NAME, transactionReceiptUser2.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER1_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER2_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("2000000000000000000"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the input was erroneous.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithWrongInput2() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser2.getTransactionHash());
		saplObject.put(WRONG_NAME, USER1_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER2_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("2000000000000000000"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the input was erroneous.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithWrongInput3() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser2.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER1_ADDRESS);
		saplObject.put(WRONG_NAME, USER2_ADDRESS);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("2000000000000000000"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the input was erroneous.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithWrongInput4() {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, transactionReceiptUser2.getTransactionHash());
		saplObject.put(FROM_ACCOUNT, USER1_ADDRESS);
		saplObject.put(TO_ACCOUNT, USER2_ADDRESS);
		saplObject.put(WRONG_NAME, new BigInteger("2000000000000000000"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the input was erroneous.", result);

	}

	// clientVersion

	@Test
	public void web3ClientVersionShouldReturnTheClientVersion() throws IOException, AttributeException {
		String pipClientVersion = ethPip.web3ClientVersion(null, null).blockFirst().asText();
		String web3jClientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
		assertEquals("The web3ClientVersion from the PIP was not loaded correctly.", pipClientVersion,
				web3jClientVersion);
	}

	// sha3
	@Test
	public void web3Sha3ShouldReturnCorrectValuer() throws IOException, AttributeException {
		JsonNode saplObject = JSON.textNode(USER3_PRIVATE_KEY);
		String pipResult = ethPip.web3Sha3(saplObject, null).blockFirst().textValue();
		String web3jResult = web3j.web3Sha3(USER3_PRIVATE_KEY).send().getResult();
		assertEquals("The web3Sha3 method did not work correctly.", pipResult, web3jResult);
	}

	// netVersion
	@Test
	public void netVersionShouldReturnCorrectValue() throws IOException, AttributeException {
		String pipResult = ethPip.netVersion(null, null).blockFirst().textValue();
		String web3jResult = web3j.netVersion().send().getNetVersion();
		assertEquals("The netVersion method did not work correctly.", pipResult, web3jResult);

	}

	// listening
	@Test
	public void netListeningShouldReturnTrueWhenListeningToNetworkConnections() throws IOException, AttributeException {
		assertTrue("The netListening method did not return true although the Client by default is listening.",
				ethPip.netListening(null, null).blockFirst().asBoolean());
	}

	// peerCount
	@Test
	public void netPeerCountShouldReturnTheCorrectNumber() throws IOException, AttributeException {
		BigInteger pipResult = ethPip.netPeerCount(null, null).blockFirst().bigIntegerValue();
		BigInteger web3jResult = web3j.netPeerCount().send().getQuantity();
		assertEquals("The netPeerCount method did not return the correct number.", pipResult, web3jResult);
	}

	// protocolVersion
	@Test
	public void protocolVersionShouldReturnTheCorrectValue() throws AttributeException, IOException {
		String pipResult = ethPip.ethProtocolVersion(null, null).blockFirst().textValue();
		String web3jResult = web3j.ethProtocolVersion().send().getProtocolVersion();
		assertEquals("The ethProtocolVersion method did not return the correct value.", pipResult, web3jResult);
	}

	// syncing
	@Test
	public void ethSyncingShouldReturnTheCorrectValue() throws AttributeException, IOException {
		boolean pipResult = ethPip.ethSyncing(null, null).blockFirst().asBoolean();
		boolean web3jResult = web3j.ethSyncing().send().getResult().isSyncing();
		assertEquals("The ethSyncing method did not return the correct value.", pipResult, web3jResult);
	}

	// coinbase
	@Test
	public void ethCoinbaseShouldReturnTheCorrectValue() throws AttributeException, IOException {
		String pipResult = ethPip.ethCoinbase(null, null).blockFirst().textValue();
		String web3jResult = web3j.ethCoinbase().send().getResult();
		assertEquals("The ethCoinbase method did not return the correct value.", pipResult, web3jResult);
	}

	// mining
	@Test
	public void ethMiningShouldReturnTheCorrectValue() throws AttributeException, IOException {
		boolean pipResult = ethPip.ethMining(null, null).blockFirst().asBoolean();
		boolean web3jResult = web3j.ethMining().send().getResult();
		assertEquals("The ethMining method did not return the correct value.", pipResult, web3jResult);
	}

	// hashrate
	@Test
	public void ethHashrateShouldReturnTheCorrectValue() throws AttributeException, IOException {
		BigInteger pipResult = ethPip.ethHashrate(null, null).blockFirst().bigIntegerValue();
		assertTrue("The ethHashrate should be greater than 0.", pipResult.intValue() > 0);
	}

	// gasPrice
	@Test
	public void ethGasPriceShouldReturnTheCorrectValue() throws AttributeException, IOException {
		BigInteger pipResult = ethPip.ethGasPrice(null, null).blockFirst().bigIntegerValue();
		BigInteger web3jResult = web3j.ethGasPrice().send().getGasPrice();
		assertEquals("The ethGasPrice method did not return the correct number.", pipResult, web3jResult);
	}

	// accounts
	@Test
	public void ethAccountsShouldReturnTheCorrectValue() throws AttributeException, IOException {

		List<JsonNode> result = new ArrayList<JsonNode>();
		ethPip.ethAccounts(null, null).blockFirst().elements().forEachRemaining(result::add);
		List<String> pipResult = result.stream().map(s -> s.toString()).collect(Collectors.toList());
		List<String> web3jResult = web3j.ethAccounts().send().getAccounts();
		assertEquals("The accounts method did not return the correct accounts.", pipResult, web3jResult);
	}

	// blockNumber
	@Test
	public void ethBlockNumberShouldReturnTheCorrectValue() throws AttributeException, IOException {
		BigInteger pipResult = ethPip.ethBlockNumber(null, null).blockFirst().bigIntegerValue();
		BigInteger web3jResult = web3j.ethBlockNumber().send().getBlockNumber();
		assertEquals("The ethBlockNumber method did not return the correct value.", pipResult, web3jResult);
	}

	// balance
	@Test
	public void ethGetBalanceShouldReturnTheCorrectValue() throws AttributeException, IOException {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(ADDRESS, USER1_ADDRESS);
		saplObject.put(DEFAULT_BLOCK_PARAMETER_STRING, LATEST);
		BigInteger pipResult = ethPip.ethGetBalance(saplObject, null).blockFirst().bigIntegerValue();
		BigInteger web3jResult = web3j.ethGetBalance(USER1_ADDRESS, DefaultBlockParameter.valueOf(LATEST)).send()
				.getBalance();
		assertEquals("The ethGetBalance method did not return the correct value.", pipResult, web3jResult);
	}

	// storage
	@Test
	public void ethGetStorageAtShouldReturnTheCorrectValue() throws AttributeException, IOException {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(ADDRESS, authContractAddress);
		saplObject.put(POSITION, BigInteger.ZERO);
		saplObject.put(DEFAULT_BLOCK_PARAMETER_STRING, LATEST);
		String pipResult = ethPip.ethGetStorageAt(saplObject, null).blockFirst().textValue();
		String web3jResult = web3j
				.ethGetStorageAt(authContractAddress, BigInteger.ZERO, DefaultBlockParameter.valueOf(LATEST)).send()
				.getData();
		assertEquals("The ethGetStorageAt method did not return the correct value.", pipResult, web3jResult);
	}

	// transactionCount
	@Test
	public void ethGetTransactionCountShouldReturnTheCorrectValue() throws AttributeException, IOException {
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(ADDRESS, USER1_ADDRESS);
		saplObject.put(DEFAULT_BLOCK_PARAMETER_STRING, LATEST);
		BigInteger pipResult = ethPip.ethGetTransactionCount(saplObject, null).blockFirst().bigIntegerValue();
		BigInteger web3jResult = web3j.ethGetTransactionCount(USER1_ADDRESS, DefaultBlockParameter.valueOf(LATEST))
				.send().getTransactionCount();
		assertEquals("The ethGetStorageAt method did not return the correct value.", pipResult, web3jResult);
	}

	// blockTransactionCountByHash
	@Test
	public void ethGetBlockTransactionCountByHashShouldReturnTheCorrectValue()
			throws AttributeException, IOException, InterruptedException, ExecutionException {
		String blockhash = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(LATEST), false).send().getBlock()
				.getHash();
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(BLOCK_HASH, blockhash);
		BigInteger pipResult = ethPip.ethGetBlockTransactionCountByHash(saplObject, null).blockFirst()
				.bigIntegerValue();
		BigInteger web3jResult = web3j.ethGetBlockTransactionCountByHash(blockhash).send().getTransactionCount();
		assertEquals("The ethGetBlockTransactionCountByHash method did not return the correct value.", pipResult,
				web3jResult);
	}

	// blockTransactionCountByNumber
	@Test
	public void ethGetBlockTransactionCountByNumberShouldReturnTheCorrectValue()
			throws AttributeException, IOException {
		BigInteger blocknumber = web3j.ethBlockNumber().send().getBlockNumber();
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(DEFAULT_BLOCK_PARAMETER_BIG_INT, blocknumber);
		BigInteger pipResult = ethPip.ethGetBlockTransactionCountByNumber(saplObject, null).blockFirst()
				.bigIntegerValue();
		BigInteger web3jResult = web3j.ethGetBlockTransactionCountByNumber(DefaultBlockParameter.valueOf(blocknumber))
				.send().getTransactionCount();
		assertEquals("The ethGetBlockTransactionCountByNumber method did not return the correct value.", pipResult,
				web3jResult);
	}

	// uncleCountByBlockHash
	@Test
	public void ethGetUncleCountByBlockHashShouldReturnTheCorrectValue() throws AttributeException, IOException {
		String blockhash = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(LATEST), false).send().getBlock()
				.getHash();
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(BLOCK_HASH, blockhash);
		BigInteger pipResult = ethPip.ethGetUncleCountByBlockHash(saplObject, null).blockFirst().bigIntegerValue();
		BigInteger web3jResult = web3j.ethGetUncleCountByBlockHash(blockhash).send().getUncleCount();
		assertEquals("The ethGetUncleCountByBlockHash method did not return the correct value.", pipResult,
				web3jResult);
	}

	// uncleCountByBlockNumber
	@Test
	public void uncleCountByBlockNumberShouldReturnTheCorrectValue() throws AttributeException, IOException {
		BigInteger blocknumber = web3j.ethBlockNumber().send().getBlockNumber();
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(DEFAULT_BLOCK_PARAMETER_BIG_INT, blocknumber);
		BigInteger pipResult = ethPip.ethGetUncleCountByBlockNumber(saplObject, null).blockFirst().bigIntegerValue();
		BigInteger web3jResult = web3j.ethGetBlockTransactionCountByNumber(DefaultBlockParameter.valueOf(blocknumber))
				.send().getTransactionCount();
		assertEquals("The ethGetUncleCountByBlockNumber method did not return the correct value.", pipResult,
				web3jResult);
	}

}
