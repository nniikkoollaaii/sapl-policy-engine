package io.sapl.interpreter.pip;

import java.util.Map;

import org.web3j.crypto.Credentials;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.functions.EthereumFunctionLibrary;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = "EthereumPIP", description = "Connects to the Ethereum Blockchain.")
public class EthereumPolicyInformationPoint {

    @Attribute(name = "verifyTransaction", docs = "Verifies if a transaction has taken place.")
    public Flux<JsonNode> verifyTransaction(JsonNode accounts, Map<String, JsonNode> variables) {
	Credentials credentials = EthereumFunctionLibrary.loadCredentials(accounts, variables);

	return Flux.just(null);
    }

}
