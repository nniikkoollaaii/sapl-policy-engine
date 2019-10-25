package io.sapl.interpreter.pip;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.math.BigInteger;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Web3j.class)
public class EthereumModuleTest {

    private static final String WRONG_NAME = "wrongName";

    private static final String TRANSACTION_VALUE = "transactionValue";

    private static final String TO_ACCOUNT = "toAccount";

    private static final String FROM_ACCOUNT = "fromAccount";

    private static final String TRANSACTION_HASH = "transactionHash";

    @Mock
    private static Web3j web3j;

    @Mock
    private static Web3jService web3jService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static EthereumPolicyInformationPoint ethPip;
    private static final JsonNodeFactory factory = new JsonNodeFactory(true);
    private static final Logger logger = LoggerFactory.getLogger(EthereumIntegrationTest.class);

    private static final String TEST_TRANSACTION_HASH = "0xbeac927d1d256e9a21f8d81233cc83c03bf1a7a79a73a4664fa7ffba74101dac";
    private static final String TEST_FALSE_TRANSACTION_HASH = "0x777c927d1d256e9a21f8d81233cc83c03bf1a7a79a73a4664fa7ffba74101dac";
    private static final String TEST_FROM_ACCOUNT = "0x70b6613e37616045a80a97e08e930e1e4d800039";
    private static final String TEST_TO_ACCOUNT = "0x3f2cbea2185089ea5bbabbcd7616b215b724885c";
    private static final String TEST_FALSE_ACCOUNT = "0x555cbea2185089ea5bbabbcd7616b215b724885c";
    private static final BigInteger TEST_TRANSACTION_VALUE = new BigInteger("2000000000000000000");

    @BeforeClass
    public static void init() {
	mockStatic(Web3j.class);
	when(Web3j.build(web3jService)).thenReturn(web3j);
	ethPip = new EthereumPolicyInformationPoint(web3jService);

    }

    // verifyTransaction

    @Test
    public void verifyTransactionShouldReturnTrueWithCorrectTransaction() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
	saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
	saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
	saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertTrue("Transaction was not validated as true although it is correct.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseValue() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
	saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
	saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
	saplObject.put(TRANSACTION_VALUE, new BigInteger("25"));
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the value was false.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseSender() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
	saplObject.put(FROM_ACCOUNT, TEST_FALSE_ACCOUNT);
	saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
	saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the sender was false.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseRecipient() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put(TRANSACTION_HASH, TEST_TRANSACTION_HASH);
	saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
	saplObject.put(TO_ACCOUNT, TEST_FALSE_ACCOUNT);
	saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the recipient was false.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseTransactionHash() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put(TRANSACTION_HASH, TEST_FALSE_TRANSACTION_HASH);
	saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
	saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
	saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the TransactionHash was false.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithNullInput() {
	boolean result = ethPip.verifyTransaction(null, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the input was null.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithWrongInput() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put(WRONG_NAME, TEST_TRANSACTION_HASH);
	saplObject.put(FROM_ACCOUNT, TEST_FROM_ACCOUNT);
	saplObject.put(TO_ACCOUNT, TEST_TO_ACCOUNT);
	saplObject.put(TRANSACTION_VALUE, TEST_TRANSACTION_VALUE);
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the input was erroneous.", result);

    }

    @Test
    public void convertToTypeShouldReturnCorrectValueWithAddressType() {

    }

}
