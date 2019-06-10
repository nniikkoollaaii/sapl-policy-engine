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
	String walletPassword = ethereumWallet.get("walletPassword").textValue();
	String walletFile = ethereumWallet.get("walletFile").textValue();
	Credentials credentials;
	try {
	    credentials = WalletUtils.loadCredentials(walletPassword, walletFile);
	    return credentials;
	} catch (IOException | CipherException e) {

	}

	return null;
    }

}
