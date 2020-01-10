package io.sapl.interpreter.pip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.methods.response.EthLog.LogObject;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.ShhMessages.SshMessage;
import org.web3j.protocol.core.methods.response.Transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@PrepareForTest({ Web3j.class, Web3jService.class })
@RunWith(PowerMockRunner.class)
public class EthereumModuleTest {

	private static final String WRONG_NAME = "wrongName";

	private static final String TRANSACTION_VALUE = "transactionValue";

	private static final String TO_ACCOUNT = "toAccount";

	private static final String FROM_ACCOUNT = "fromAccount";

	private static final String TRANSACTION_HASH = "transactionHash";

	private static final String TEST_TRANSACTION_HASH = "0xbeac927d1d256e9a21f8d81233cc83c03bf1a7a79a73a4664fa7ffba74101dac";

	private static final String TEST_FALSE_TRANSACTION_HASH = "0x777c927d1d256e9a21f8d81233cc83c03bf1a7a79a73a4664fa7ffba74101dac";

	private static final String TEST_FROM_ACCOUNT = "0x70b6613e37616045a80a97e08e930e1e4d800039";

	private static final String TEST_TO_ACCOUNT = "0x3f2cbea2185089ea5bbabbcd7616b215b724885c";

	private static final String TEST_FALSE_ACCOUNT = "0x555cbea2185089ea5bbabbcd7616b215b724885c";

	private static final BigInteger TEST_TRANSACTION_VALUE = new BigInteger("2000000000000000000");

	private static final String HTTP_LOCALHOST = "http://localhost:";

	private static final String CERTIFICATION = "certification";

	private static final String HAS_CERTIFICATE = "hasCertificate";

	private static final String ACCESS = "access";

	private static final String ETHEREUM = "ethereum";

	private static final String OUTPUT_PARAMS = "outputParams";

	private static final String BOOL = "bool";

	private static final String INPUT_PARAMS = "inputParams";

	private static final String VALUE = "value";

	private static final String ADDRESS = "address";

	private static final String TYPE = "type";

	private static final String CONTRACT_ADDRESS = "contractAddress";

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

	private static final String SHA3_HASH_OF_DATA_TO_SIGN = "sha3HashOfDataToSign";

	private static final String FILTER_ID = "filterId";

	private static final String TEST_DATA_CLIENT_VERSION = "besu/v1.3.5/linux-x86_64/oracle_openjdk-java-11";

	private static final String TEST_DATA_SIGN_ADDRESS = "0x9b2055d370f73ec7d8a03e965129118dc8f5bf83";

	private static final String TEST_DATA_SIGN_MESSAGE = "0xdeadbeaf";

	private static final String TEST_DATA_SIGN_RESULT = "0xa3f20717a250c2b0b729b7e5becbff67fdaef7e0699da4de7ca5895b02a170a12d887fd3b17bfdce3481f10bea41f45ba9f709d39ce8325427b57afcfc994cee1b";

	private static final String TEST_DATA_SHH_VERSION = "2";

	private static final String TEST_DATA_HAS_IDENTITY = "0x04f96a5e25610293e42a73908e93ccc8c4d4dc0edcfa9fa872f50cb214e08ebf61a03e245533f97284d442460f2998cd41858798ddfd4d661997d3940272b717b1";

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static EthereumPolicyInformationPoint ethPip;

	@Mock
	private static Web3jService web3jService;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Web3j web3j;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private EthTransaction ethTransaction;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Transaction transactionFromChain;

	private Optional<Transaction> optionalTransactionFromChain;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Before
	public void init() throws IOException {
		mockStatic(Web3j.class);
		ethPip = new EthereumPolicyInformationPoint(web3j);
	}

	// verifyTransaction

	@Test
	public void verifyTransactionShouldReturnTrueWithCorrectTransaction() throws IOException {
		optionalTransactionFromChain = Optional.of(transactionFromChain);
		when(web3j.ethGetTransactionByHash(TEST_TRANSACTION_HASH).send()).thenReturn(ethTransaction);
		when(ethTransaction.getTransaction()).thenReturn(optionalTransactionFromChain);
		when(transactionFromChain.getFrom()).thenReturn(TEST_FROM_ACCOUNT);
		when(transactionFromChain.getTo()).thenReturn(TEST_TO_ACCOUNT);
		when(transactionFromChain.getValue()).thenReturn(TEST_TRANSACTION_VALUE);
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
		saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
		saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
		saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertTrue("Transaction was not validated as true although it is correct.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseValue() throws IOException {
		optionalTransactionFromChain = Optional.of(transactionFromChain);
		when(web3j.ethGetTransactionByHash(TEST_TRANSACTION_HASH).send()).thenReturn(ethTransaction);
		when(ethTransaction.getTransaction()).thenReturn(optionalTransactionFromChain);
		when(transactionFromChain.getFrom()).thenReturn(TEST_FROM_ACCOUNT);
		when(transactionFromChain.getTo()).thenReturn(TEST_TO_ACCOUNT);
		when(transactionFromChain.getValue()).thenReturn(TEST_TRANSACTION_VALUE);
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
		saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
		saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
		saplObject.put(TRANSACTION_VALUE, new BigInteger("25"));
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the value was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseSender() throws IOException {
		optionalTransactionFromChain = Optional.of(transactionFromChain);
		when(web3j.ethGetTransactionByHash(TEST_TRANSACTION_HASH).send()).thenReturn(ethTransaction);
		when(ethTransaction.getTransaction()).thenReturn(optionalTransactionFromChain);
		when(transactionFromChain.getFrom()).thenReturn(TEST_FROM_ACCOUNT);
		when(transactionFromChain.getValue()).thenReturn(TEST_TRANSACTION_VALUE);
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
		saplObject.put(FROM_ACCOUNT, TEST_FALSE_ACCOUNT);
		saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
		saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the sender was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseRecipient() throws IOException {
		optionalTransactionFromChain = Optional.of(transactionFromChain);
		when(web3j.ethGetTransactionByHash(TEST_TRANSACTION_HASH).send()).thenReturn(ethTransaction);
		when(ethTransaction.getTransaction()).thenReturn(optionalTransactionFromChain);
		when(transactionFromChain.getFrom()).thenReturn(TEST_FROM_ACCOUNT);
		when(transactionFromChain.getTo()).thenReturn(TEST_TO_ACCOUNT);
		when(transactionFromChain.getValue()).thenReturn(TEST_TRANSACTION_VALUE);
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
		saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
		saplObject.put(TO_ACCOUNT, TEST_FALSE_ACCOUNT);
		saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the recipient was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithFalseTransactionHash() throws IOException {
		when(web3j.ethGetTransactionByHash(TEST_TRANSACTION_HASH).send()).thenReturn(ethTransaction);
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(TRANSACTION_HASH, TEST_FALSE_TRANSACTION_HASH);
		saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
		saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
		saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the TransactionHash was false.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithNullInput() throws IOException {
		when(web3j.ethGetTransactionByHash(TEST_TRANSACTION_HASH).send()).thenReturn(ethTransaction);
		when(transactionFromChain.getValue()).thenReturn(TEST_TRANSACTION_VALUE);
		boolean result = ethPip.verifyTransaction(null, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the input was null.", result);

	}

	@Test
	public void verifyTransactionShouldReturnFalseWithWrongInput() throws IOException {
		when(web3j.ethGetTransactionByHash(TEST_TRANSACTION_HASH).send()).thenReturn(ethTransaction);
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(WRONG_NAME, TEST_TRANSACTION_HASH);
		saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
		saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
		saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
		boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
		assertFalse("Transaction was not validated as false although the input was erroneous.", result);

	}

	// loadContractInformation

	// clientVersion

	@Test
	public void web3ClientVersionShouldReturnTheClientVersion() throws IOException {
		when(web3j.web3ClientVersion().send().getWeb3ClientVersion()).thenReturn(TEST_DATA_CLIENT_VERSION);
		String pipResult = ethPip.web3ClientVersion(null, null).blockFirst().asText();
		assertEquals("The web3ClientVersion from the PIP was not loaded correctly.", TEST_DATA_CLIENT_VERSION,
				pipResult);
	}

	// sign
	@Test
	public void ethSignShouldReturnTheCorrectValue() throws IOException {
		when(web3j.ethSign(TEST_DATA_SIGN_ADDRESS, TEST_DATA_SIGN_MESSAGE).send().getSignature())
				.thenReturn(TEST_DATA_SIGN_RESULT);

		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(ADDRESS, TEST_DATA_SIGN_ADDRESS);
		saplObject.put(SHA3_HASH_OF_DATA_TO_SIGN, TEST_DATA_SIGN_MESSAGE);
		String pipResult = ethPip.ethSign(saplObject, null).blockFirst().textValue();
		assertEquals("The ethSign method did not return the correct value.", TEST_DATA_SIGN_RESULT, pipResult);
	}

	// shhFilterChanges
	@Test
	public void shhFilterChangesShouldReturnCorrectValue() throws IOException {

		BigInteger filterId = BigInteger.valueOf(7L);
		List<SshMessage> sshList = new ArrayList<>();
		sshList.add(createTestMessage());

		when(web3j.shhGetFilterChanges(filterId).send().getMessages()).thenReturn(sshList);

		JsonNode saplObject = JSON.numberNode(filterId);
		JsonNode pipList = ethPip.shhGetFilterChanges(saplObject, null).blockFirst();
		List<String> pipResult = new ArrayList<>();
		for (JsonNode json : pipList) {
			pipResult.add(json.toString());
		}

		List<String> sshStringList = new ArrayList<>();
		for (SshMessage sshMessage : sshList) {
			sshStringList.add(mapper.convertValue(sshMessage, JsonNode.class).toString());
		}
		assertEquals("The shhGetFilterChanges method did not work correctly.", sshStringList, pipResult);

	}

	// ethFilterLogs
	@Test
	public void ethGetFilterLogsShouldReturnTheCorrectValue() throws IOException {

		BigInteger filterId = BigInteger.valueOf(22L);
		ObjectNode saplObject = JSON.objectNode();
		saplObject.put(FILTER_ID, filterId);

		when(web3j.ethGetFilterLogs(filterId).send().getLogs()).thenReturn(Arrays.asList(createLogObject()));

		JsonNode pipList = ethPip.ethGetFilterLogs(saplObject, null).blockFirst();
		List<String> pipResult = new ArrayList<>();
		for (JsonNode json : pipList) {
			pipResult.add(json.toString());
		}

		List<String> logStringList = Arrays.asList(mapper.convertValue(createLogObject(), JsonNode.class).toString());

		assertEquals("The ethGetFilterLogs method did not return the correct value.", logStringList, pipResult);
	}

	// shhVersion
	@Test
	public void shhVersionShouldReturnCorrectValue() throws IOException {
		when(web3j.shhVersion().send().getVersion()).thenReturn(TEST_DATA_SHH_VERSION);
		String pipResult = ethPip.shhVersion(null, null).blockFirst().textValue();
		assertEquals("The shhVersion method did not work correctly.", TEST_DATA_SHH_VERSION, pipResult);

	}

	// work
	@Test
	public void ethGetWorkShouldReturnCorrectValue() throws IOException {
		List<String> ethWorkList = getEthWorkList();
		when(web3j.ethGetWork().send().getResult()).thenReturn(ethWorkList);

		JsonNode pipList = ethPip.ethGetWork(null, null).blockFirst();
		List<String> pipResult = new ArrayList<>();
		for (JsonNode json : pipList) {
			pipResult.add(json.textValue());
		}
		assertEquals("The ethGetWork method did not work correctly.", ethWorkList, pipResult);

	}

	// hasIdentity
	@Test
	public void shhHasIdentityShouldReturnCorrectValue() throws IOException {

		JsonNode saplObject = JSON.textNode(TEST_DATA_HAS_IDENTITY);

		when(web3j.shhHasIdentity(TEST_DATA_HAS_IDENTITY).send().getResult()).thenReturn(true);

		boolean pipResult = ethPip.shhHasIdentity(saplObject, null).blockFirst().asBoolean();
		assertTrue("The shhHasIdentity method did not work correctly.", pipResult);

	}

	private static SshMessage createTestMessage() {
		return new SshMessage("0x33eb2da77bf3527e28f8bf493650b1879b08c4f2a362beae4ba2f71bafcd91f9",
				"0xc931d93e97ab07fe42d923478ba2465f283f440fd6cabea4dd7a2c807108f651b7135d1d6ca9007d5b68aa497e4619ac10aa3b27726e1863c1fd9b570d99bbaf",
				"0x04f96a5e25610293e42a73908e93ccc8c4d4dc0edcfa9fa872f50cb214e08ebf61a03e245533f97284d442460f2998cd41858798ddfd4d661997d3940272b717b1",
				"0x54caa50a", "0x64", "0x54ca9ea2", Arrays.asList("0x6578616d"), "0x12345678", "0x0");
	}

	private static LogObject createLogObject() {
		return new LogObject(false, "0x1", "0x0", "0xdf829c5a142f1fccd7d8216c5785ac562ff41e2dcfdf5785ac562ff41e2dcf",
				"0x8216c5785ac562ff41e2dcfdf5785ac562ff41e2dcfdf829c5a142f1fccd7d", "0x1b4",
				"0x16c5785ac562ff41e2dcfdf829c5a142f1fccd7d",
				"0x0000000000000000000000000000000000000000000000000000000000000000", "0x0",
				Arrays.asList("0x59ebeb90bc63057b6515673c3ecf9438e5058bca0f92585014eced636878c9a5"));
	}

	private static List<String> getEthWorkList() {
		return Arrays.asList("0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef",
				"0x5EED00000000000000000000000000005EED0000000000000000000000000000",
				"0xd1ff1c01710000000000000000000000d1ff1c01710000000000000000000000");
	}

}
