package io.sapl.interpreter.pip;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.primitive.Char;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pip.AttributeException;

public class EthereumPipFunctionsTest {

	private static final String SOME_STRING = "someString";

	private static final String STRING = "string";

	private static final String BOOL = "bool";

	private static final String ADDRESS = "address";

	private static final String TEST_ADDRESS = "0x3f2cbea2185089ea5bbabbcd7616b215b724885c";

	private static final String TYPE = "type";

	private static final String VALUE = "value";

	private static final byte[] byteArray = hexStringToByteArray(TEST_ADDRESS);

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
		inputParam.put(TYPE, ADDRESS);
		inputParam.put(VALUE, TEST_ADDRESS);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Address correctly.", result, new Address(TEST_ADDRESS));
	}

	@Test
	public void convertToTypeShouldReturnBoolTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, BOOL);
		inputParam.put(VALUE, true);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bool correctly.", result, new Bool(true));
	}

	@Test
	public void convertToTypeShouldReturnStringTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, STRING);
		inputParam.put(VALUE, SOME_STRING);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the String correctly.", result, new Utf8String(SOME_STRING));
	}

	@Test
	public void convertToTypeShouldReturnBytesTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "bytes");
		inputParam.put(VALUE, byteArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the DynamicBytes correctly.", result, new DynamicBytes(byteArray));
	}

	@Test
	public void convertToTypeShouldReturnByteTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		byte testByte = 125;
		inputParam.put(TYPE, "byte");
		inputParam.put(VALUE, testByte);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Byte correctly.", result,
				new org.web3j.abi.datatypes.primitive.Byte(testByte));
	}

	@Test
	public void convertToTypeShouldReturnCharTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "char");
		inputParam.put(VALUE, "a");
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Char correctly.", result, new Char('a'));
	}

	@Test(expected = AttributeException.class)
	public void convertToTypeCharShouldWorkWithEmptyString() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "char");
		inputParam.put(VALUE, "");
		Type result = EthereumPipFunctions.convertToType(inputParam);
	}

	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

}
