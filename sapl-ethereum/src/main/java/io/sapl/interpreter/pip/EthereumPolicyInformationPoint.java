package io.sapl.interpreter.pip;

import java.io.IOException;
import java.util.Map;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = "EthereumPIP", description = "Connects to the Ethereum Blockchain.")
public class EthereumPolicyInformationPoint {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonNode trueNode = mapper.convertValue(true, JsonNode.class);
    private static final JsonNode falseNode = mapper.convertValue(false, JsonNode.class);

    @Attribute(name = "verifyTransaction", docs = "Returns true, if a transaction has taken place and false otherwise.")
    public Flux<JsonNode> verifyTransaction(JsonNode transactionToVerify, Map<String, JsonNode> variables) {
	Web3j web3j = Web3j.build(new HttpService());
	try {

	    EthTransaction ethTransaction = web3j
		    .ethGetTransactionByHash(transactionToVerify.get("transactionHash").textValue()).send();
	    Transaction transactionFromChain = ethTransaction.getTransaction().get();
	    if (transactionFromChain.getFrom().equals(transactionToVerify.get("fromAccount").textValue())
		    && transactionFromChain.getTo().equals(transactionToVerify.get("toAccount").textValue())
		    && transactionFromChain.getValue().toString()
			    .equals(transactionToVerify.get("transactionValue").textValue())) {
		return Flux.just(trueNode);
	    }
	} catch (IOException e) {

	}

	return Flux.just(falseNode);
    }

    @Attribute(name = "loadContractInformation", docs = "Load information from a contract.")
    public Flux<JsonNode> loadContractInformation(JsonNode contract, Map<String, JsonNode> variables) {

	Web3j web3j = Web3j.build(new HttpService());

	JsonNode node = null;
	return Flux.just(node);
    }

}
