package io.sapl.interpreter.pip;

import java.util.Map;

import org.web3j.crypto.Credentials;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.interpreter.pip.ethereum.EthereumPipFunctions;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = "EthereumPIP", description = "Connects to the Ethereum Blockchain.")
public class EthereumPolicyInformationPoint {

    @Attribute(name = "verifyTransaction", docs = "Verifies if a transaction has taken place.")
    public Flux<JsonNode> verifyTransaction(JsonNode accounts, Map<String, JsonNode> variables) {
	Credentials credentials = EthereumPipFunctions.loadCredentials(accounts, variables);

	JsonNode node = null;
	return Flux.just(node);
    }

    @Attribute(name = "loadContractInformation", docs = "Load information from a contract.")
    public Flux<JsonNode> loadContractInformation(JsonNode contract, Map<String, JsonNode> variables) {

	JsonNode node = null;
	return Flux.just(node);
    }

}
