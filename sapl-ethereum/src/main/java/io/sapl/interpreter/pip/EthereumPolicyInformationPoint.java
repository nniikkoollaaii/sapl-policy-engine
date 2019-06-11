package io.sapl.interpreter.pip;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.interpreter.pip.ethereum.EthereumPipFunctions;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = "EthereumPIP", description = "Connects to the Ethereum Blockchain.")
public class EthereumPolicyInformationPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Attribute(name = "verifyTransaction", docs = "Returns true, if a transaction has taken place and false otherwise.")
    public Flux<JsonNode> verifyTransaction(JsonNode transaction, Map<String, JsonNode> variables) {
	Web3j web3j = Web3j.build(new HttpService());
	Credentials credentials = EthereumPipFunctions.loadCredentials(transaction, variables);
	try {
	    EthGetTransactionReceipt receipt = web3j
		    .ethGetTransactionReceipt(transaction.get("transactionHash").textValue()).send();
	    Optional<TransactionReceipt> optionalTransactionReceipt = receipt.getTransactionReceipt();
	    TransactionReceipt transactionReceipt = optionalTransactionReceipt.get();
	    transactionReceipt.getFrom();
	    transactionReceipt.getTo();
	} catch (IOException e) {

	}

	JsonNode falseNode = mapper.convertValue(false, JsonNode.class);
	return Flux.just(falseNode);
    }

    @Attribute(name = "loadContractInformation", docs = "Load information from a contract.")
    public Flux<JsonNode> loadContractInformation(JsonNode contract, Map<String, JsonNode> variables) {

	JsonNode node = null;
	return Flux.just(node);
    }

}
