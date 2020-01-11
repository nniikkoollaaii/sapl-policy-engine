package io.sapl.interpreter.pip;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthereumBasicFunctions {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	protected static JsonNode convertToJsonNode(Object o) {
		return mapper.convertValue(o, JsonNode.class);
	}

	protected static BigInteger getBigIntFrom(JsonNode saplObject, String bigIntegerName) {
		if (saplObject.has(bigIntegerName)) {
			return saplObject.get(bigIntegerName).bigIntegerValue();
		}
		LOGGER.warn("The input JsonNode for the policy didn't contain a field of type " + bigIntegerName
				+ ", altough this was expected. Ignore this message if the field was optional.");
		return null;
	}

	protected static boolean getBooleanFrom(JsonNode saplObject, String booleanName) {
		if (saplObject.has(booleanName)) {
			return saplObject.get(booleanName).asBoolean();
		}
		LOGGER.warn("The input JsonNode for the policy didn't contain a field of type " + booleanName
				+ ", altough this was expected. Ignore this message if the field was optional.");
		return false;
	}

	protected static JsonNode getJsonFrom(JsonNode saplObject, String jsonName) {
		if (saplObject.has(jsonName)) {
			return saplObject.get(jsonName);
		}
		LOGGER.warn("The input JsonNode for the policy didn't contain a field of type " + jsonName
				+ ", altough this was expected. Ignore this message if the field was optional.");
		return JSON.nullNode();
	}

	protected static List<TypeReference<?>> getOutputParameters(JsonNode outputNode) throws ClassNotFoundException {
		List<TypeReference<?>> outputParameters = new ArrayList<>();
		if (outputNode.isArray()) {
			for (JsonNode solidityType : outputNode) {
				outputParameters.add(TypeReference.makeTypeReference(solidityType.textValue()));
			}
			return outputParameters;
		}
		LOGGER.warn("The JsonNode containing the ouput parameters wasn't an array as expected. "
				+ "An empty list is being returned.");
		return outputParameters;
	}

	protected static List<JsonNode> getJsonList(JsonNode inputParams) {
		List<JsonNode> inputList = new ArrayList<>();
		if (inputParams.isArray()) {
			for (JsonNode inputParam : inputParams) {
				inputList.add(inputParam);
			}
			return inputList;
		}
		LOGGER.warn("The JsonNode containing the input parameters wasn't an array as expected. "
				+ "An empty list is being returned.");
		return inputList;
	}

	protected static Function createFunction(String functionName, JsonNode inputParams, JsonNode outputParams)
			throws ClassNotFoundException {
		return new Function(functionName,
				getJsonList(inputParams).stream().map(EthereumPipFunctions::convertToType).collect(Collectors.toList()),
				getOutputParameters(outputParams));
	}

	protected static List<String> getStringListFrom(JsonNode saplObject, String listName) {
		if (saplObject.has(listName)) {
			List<String> returnList = new ArrayList<>();
			JsonNode array = saplObject.get(listName);
			if (array.isArray()) {
				array.forEach(s -> returnList.add(s.textValue()));
			}
			return returnList;
		}
		LOGGER.warn("The input JsonNode for the policy didn't contain a field of type " + listName
				+ ", altough this was expected. Ignore this message if the field was optional.");
		return null;
	}

	protected static String getStringFrom(JsonNode saplObject, String stringName) {
		if (saplObject.has(stringName)) {
			return saplObject.get(stringName).textValue();
		}
		LOGGER.warn("The input JsonNode for the policy didn't contain a field of type " + stringName
				+ ", altough this was expected. Ignore this message if the field was optional.");
		return null;
	}

}
