package io.sapl.interpreter.pip;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pip.AttributeException;

public class EthereumPipFunctionsTest {

	private static final String TYPE = "type";

	private static final String VALUE = "value";

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	@Test(expected = AttributeException.class)
	public void convertToTypeShouldThrowAttributeExceptionIfTypeIsNotPresent() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(VALUE, 25);
		EthereumPipFunctions.convertToType(inputParam);

	}

	@Test(expected = AttributeException.class)
	public void convertToTypeShouldThrowAttributeExceptionIfValueIsNotPresent() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "aString");
		EthereumPipFunctions.convertToType(inputParam);

	}

	@Test(expected = AttributeException.class)
	public void convertToTypeShouldThrowAttributeExceptionWithNullInput() throws IOException, AttributeException {
		EthereumPipFunctions.convertToType(null);

	}

	@Test
	public void convertToTypeShouldReturnAddressTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "address");
		inputParam.put(VALUE, "0x235984236");
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Address correctly.", result, new Address("0x235984236"));
	}

}
