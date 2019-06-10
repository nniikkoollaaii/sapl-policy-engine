package io.sapl.interpreter.pip.ethereum;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class EthereumPipFunctions {

    private static final Logger logger = LoggerFactory.getLogger(EthereumPipFunctions.class);

    private static final String ETHEREUM_WALLET = "ethereumWallet";

    private static final String WALLET_PASSOWRD = "walletPassword";

    private static final String WALLET_FILE = "walletFile";

    public static Credentials loadCredentials(JsonNode saplObject, Map<String, JsonNode> variables) {

	// First trying to load Credentials that only apply with the given policy.
	if (saplObject.has(ETHEREUM_WALLET)) {
	    return retrieveCredentials(saplObject.get(ETHEREUM_WALLET));
	}

	if (variables.containsKey(ETHEREUM_WALLET)) {
	    return retrieveCredentials(variables.get(ETHEREUM_WALLET));
	}

	return null;
    }

    private static Credentials retrieveCredentials(JsonNode ethereumWallet) {
	String walletPassword = ethereumWallet.get(WALLET_PASSOWRD).textValue();
	String walletFile = ethereumWallet.get(WALLET_FILE).textValue();
	try {
	    Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletFile);
	    return credentials;
	} catch (IOException | CipherException e) {
	    logger.warn("Could not load Credentials. Please ensure that your "
		    + "credentials are annotated correctly either in the policy or in the " + "pdp.json file.");

	}

	return null;
    }

}
