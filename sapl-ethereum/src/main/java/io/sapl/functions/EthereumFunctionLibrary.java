package io.sapl.functions;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class EthereumFunctionLibrary {

    private static final Logger logger = LoggerFactory.getLogger(EthereumFunctionLibrary.class);

    public static Credentials loadCredentials(JsonNode saplObject, Map<String, JsonNode> variables) {

	// First trying to load Credentials that only apply with the given policy.
	try {
	    JsonNode ethereumWallet = saplObject.get("ethereumWallet");
	    String walletPassword = ethereumWallet.get("walletPassword").textValue();
	    String walletFile = ethereumWallet.get("walletFile").textValue();
	    Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletFile);
	    return credentials;
	} catch (NullPointerException | IOException | CipherException e) {
	    logger.debug("Didn't load credentials from policy");
	}

	return null;
    }

}
