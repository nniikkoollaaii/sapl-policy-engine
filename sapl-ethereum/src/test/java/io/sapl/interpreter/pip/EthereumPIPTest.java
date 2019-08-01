package io.sapl.interpreter.pip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EthereumPIPTest {

    private static final String KEYSTORE = "ethereum-testnet/ptn/keystore/";

    private static final String USER1WALLET = "UTC--2019-05-10T11-32-05.64000000Z--70b6613e37616045a80a97e08e930e1e4d800039.json";
    private static final String USER2WALLET = "UTC--2019-05-10T11-32-55.438000000Z--3f2cbea2185089ea5bbabbcd7616b215b724885c.json";
    private static final String USER3WALLET = "UTC--2019-05-10T11-33-01.363000000Z--2978263a3ecacb01c75e51e3f74b37016ee3904c.json";
    private static final String USER4WALLET = "UTC--2019-05-10T11-33-10.665000000Z--23a28c4cbad79cf61c8ad2e47d5134b06ef0bb73.json";

    private Web3j web3j;
    private EthereumPolicyInformationPoint ethPip;
    private static final JsonNodeFactory factory = new JsonNodeFactory(true);
    private static final Logger logger = LoggerFactory.getLogger(EthereumPIPTest.class);

    private String user1Adress;
    private String user2Adress;
    private String user3Adress;
    private String user4Adress;

    private TransactionReceipt transactionReceiptUser2;
    private TransactionReceipt transactionReceiptUser3;
    private TransactionReceipt transactionReceiptUser4;

    // TEST INFORMATION: Before launching the test please start the local Ethereum
    // private testnet via one of the startChain scripts
    // inside the folder ethereum-testnet.
    // For the scripts to work properly please follow these steps:
    // 1. Download and install Geth (https://geth.ethereum.org/downloads/) (This has
    // been tested with version 1.8.27-stable).
    // 2. Navigate to the ethereum-testnet folder inside the project in a terminal
    // or the PowerShell.
    // 3. Execute the startChain.ps1 script in Windows or the startChain script in
    // Linux to initialize and start a private, local version of the Ethereum
    // blockchain.
    // 4. Run the test.
    // 5. After the test has finished, type exit in the Geth console to stop the
    // blockchain. If you used the script, the leftovers of the blockchain should be
    // automatically deleted.

    @Before
    public void init() throws InterruptedException, TransactionException, Exception {
	web3j = Web3j.build(new HttpService());
	ethPip = new EthereumPolicyInformationPoint(new HttpService());

	// TODO Automatically start a local Ethereum private testnet

	// In this first section we load the accounts from the blockchain
	List<String> accounts;

	accounts = web3j.ethAccounts().send().getAccounts();
	user1Adress = accounts.get(0);
	user2Adress = accounts.get(1);
	user3Adress = accounts.get(2);
	user4Adress = accounts.get(3);

	// Now we make some transactions
	Credentials credentials = WalletUtils.loadCredentials("", KEYSTORE + USER1WALLET);
	transactionReceiptUser2 = Transfer
		.sendFunds(web3j, credentials, user2Adress, BigDecimal.valueOf(2.0), Convert.Unit.ETHER).send();
	transactionReceiptUser3 = Transfer
		.sendFunds(web3j, credentials, user3Adress, BigDecimal.valueOf(3.3), Convert.Unit.ETHER).send();
	transactionReceiptUser4 = Transfer
		.sendFunds(web3j, credentials, user4Adress, BigDecimal.valueOf(4.444), Convert.Unit.ETHER).send();

    }

    // verifyTransaction

    @Test
    public void verifyTransactionShouldReturnTrueWithCorrectTransaction() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put("transactionHash", transactionReceiptUser2.getTransactionHash());
	saplObject.put("fromAccount", user1Adress);
	saplObject.put("toAccount", user2Adress);
	saplObject.put("transactionValue", new BigInteger("2000000000000000000"));
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertTrue("Transaction was not validated as true although it is correct.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseValue() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put("transactionHash", transactionReceiptUser2.getTransactionHash());
	saplObject.put("fromAccount", user1Adress);
	saplObject.put("toAccount", user2Adress);
	saplObject.put("transactionValue", new BigInteger("25"));
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the value was false.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseSender() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put("transactionHash", transactionReceiptUser2.getTransactionHash());
	saplObject.put("fromAccount", user3Adress);
	saplObject.put("toAccount", user2Adress);
	saplObject.put("transactionValue", new BigInteger("2000000000000000000"));
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the sender was false.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseRecipient() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put("transactionHash", transactionReceiptUser2.getTransactionHash());
	saplObject.put("fromAccount", user1Adress);
	saplObject.put("toAccount", user3Adress);
	saplObject.put("transactionValue", new BigInteger("2000000000000000000"));
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the recipient was false.", result);

    }

    @Test
    public void verifyTransactionShouldReturnFalseWithFalseTransactionHash() {
	ObjectNode saplObject = factory.objectNode();
	saplObject.put("transactionHash", transactionReceiptUser3.getTransactionHash());
	saplObject.put("fromAccount", user1Adress);
	saplObject.put("toAccount", user2Adress);
	saplObject.put("transactionValue", new BigInteger("2000000000000000000"));
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
	saplObject.put("wrongName", transactionReceiptUser2.getTransactionHash());
	saplObject.put("fromAccount", user1Adress);
	saplObject.put("toAccount", user2Adress);
	saplObject.put("transactionValue", new BigInteger("2000000000000000000"));
	boolean result = ethPip.verifyTransaction(saplObject, null).blockFirst().asBoolean();
	assertFalse("Transaction was not validated as false although the input was erroneous.", result);

    }

    // web3_clientVersion

    @Test
    public void web3ClientVersionShouldReturnTheClientVersion() throws IOException {
	String pipClientVersion = ethPip.web3ClientVersion(null, null).blockFirst().asText();
	String web3jClientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
	assertEquals("The web3ClientVersion from the PIP was not loaded correctly.", pipClientVersion,
		web3jClientVersion);
    }

}
