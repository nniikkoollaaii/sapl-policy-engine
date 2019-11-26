package io.sapl.interpreter.pip;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Int;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes10;
import org.web3j.abi.datatypes.generated.Bytes11;
import org.web3j.abi.datatypes.generated.Bytes12;
import org.web3j.abi.datatypes.generated.Bytes13;
import org.web3j.abi.datatypes.generated.Bytes14;
import org.web3j.abi.datatypes.generated.Bytes15;
import org.web3j.abi.datatypes.generated.Bytes16;
import org.web3j.abi.datatypes.generated.Bytes17;
import org.web3j.abi.datatypes.generated.Bytes18;
import org.web3j.abi.datatypes.generated.Bytes19;
import org.web3j.abi.datatypes.generated.Bytes2;
import org.web3j.abi.datatypes.generated.Bytes20;
import org.web3j.abi.datatypes.generated.Bytes21;
import org.web3j.abi.datatypes.generated.Bytes22;
import org.web3j.abi.datatypes.generated.Bytes23;
import org.web3j.abi.datatypes.generated.Bytes24;
import org.web3j.abi.datatypes.generated.Bytes25;
import org.web3j.abi.datatypes.generated.Bytes26;
import org.web3j.abi.datatypes.generated.Bytes27;
import org.web3j.abi.datatypes.generated.Bytes28;
import org.web3j.abi.datatypes.generated.Bytes29;
import org.web3j.abi.datatypes.generated.Bytes3;
import org.web3j.abi.datatypes.generated.Bytes30;
import org.web3j.abi.datatypes.generated.Bytes31;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Bytes5;
import org.web3j.abi.datatypes.generated.Bytes6;
import org.web3j.abi.datatypes.generated.Bytes7;
import org.web3j.abi.datatypes.generated.Bytes8;
import org.web3j.abi.datatypes.generated.Bytes9;
import org.web3j.abi.datatypes.generated.Int104;
import org.web3j.abi.datatypes.generated.Int112;
import org.web3j.abi.datatypes.generated.Int120;
import org.web3j.abi.datatypes.generated.Int128;
import org.web3j.abi.datatypes.generated.Int136;
import org.web3j.abi.datatypes.generated.Int144;
import org.web3j.abi.datatypes.generated.Int152;
import org.web3j.abi.datatypes.generated.Int16;
import org.web3j.abi.datatypes.generated.Int160;
import org.web3j.abi.datatypes.generated.Int168;
import org.web3j.abi.datatypes.generated.Int176;
import org.web3j.abi.datatypes.generated.Int184;
import org.web3j.abi.datatypes.generated.Int192;
import org.web3j.abi.datatypes.generated.Int200;
import org.web3j.abi.datatypes.generated.Int208;
import org.web3j.abi.datatypes.generated.Int216;
import org.web3j.abi.datatypes.generated.Int224;
import org.web3j.abi.datatypes.generated.Int232;
import org.web3j.abi.datatypes.generated.Int24;
import org.web3j.abi.datatypes.generated.Int240;
import org.web3j.abi.datatypes.generated.Int248;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Int32;
import org.web3j.abi.datatypes.generated.Int40;
import org.web3j.abi.datatypes.generated.Int48;
import org.web3j.abi.datatypes.generated.Int56;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.abi.datatypes.generated.Int72;
import org.web3j.abi.datatypes.generated.Int8;
import org.web3j.abi.datatypes.generated.Int80;
import org.web3j.abi.datatypes.generated.Int88;
import org.web3j.abi.datatypes.generated.Int96;
import org.web3j.abi.datatypes.generated.Uint104;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint120;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint136;
import org.web3j.abi.datatypes.generated.Uint144;
import org.web3j.abi.datatypes.generated.Uint152;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint160;
import org.web3j.abi.datatypes.generated.Uint168;
import org.web3j.abi.datatypes.generated.Uint176;
import org.web3j.abi.datatypes.generated.Uint184;
import org.web3j.abi.datatypes.generated.Uint192;
import org.web3j.abi.datatypes.generated.Uint200;
import org.web3j.abi.datatypes.generated.Uint208;
import org.web3j.abi.datatypes.generated.Uint216;
import org.web3j.abi.datatypes.generated.Uint224;
import org.web3j.abi.datatypes.generated.Uint232;
import org.web3j.abi.datatypes.generated.Uint24;
import org.web3j.abi.datatypes.generated.Uint240;
import org.web3j.abi.datatypes.generated.Uint248;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint40;
import org.web3j.abi.datatypes.generated.Uint48;
import org.web3j.abi.datatypes.generated.Uint56;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint72;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.abi.datatypes.generated.Uint80;
import org.web3j.abi.datatypes.generated.Uint88;
import org.web3j.abi.datatypes.generated.Uint96;
import org.web3j.abi.datatypes.primitive.Char;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pip.AttributeException;

@SuppressWarnings("rawtypes")
public class EthereumPipFunctionsTest {

	private static final String ETHEREUM_WALLET = "ethereumWallet";

	private static final String WALLET_PASS = "walletPassword";

	private static final String WALLET_FILE = "walletFile";

	private static final int INT_TEST_VALUE = 123;

	private static final int UINT_TEST_VALUE = 222;

	private static final String SOME_STRING = "someString";

	private static final String STRING = "string";

	private static final String BOOL = "bool";

	private static final String ADDRESS = "address";

	private static final String TEST_ADDRESS = "0x3f2cbea2185089ea5bbabbcd7616b215b724885c";

	private static final String TYPE = "type";

	private static final String VALUE = "value";

	private static final String KEYSTORE = "ethereum-testnet/ptn/keystore/";

	private static final String USER1WALLET = "UTC--2019-05-10T11-32-05.64000000Z--70b6613e37616045a80a97e08e930e1e4d800039.json";

	private static final String USER2WALLET = "UTC--2019-05-10T11-32-55.438000000Z--3f2cbea2185089ea5bbabbcd7616b215b724885c.json";

	private static final String USER3WALLET = "UTC--2019-05-10T11-33-01.363000000Z--2978263a3ecacb01c75e51e3f74b37016ee3904c.json";

	private static final String USER4WALLET = "UTC--2019-05-10T11-33-10.665000000Z--23a28c4cbad79cf61c8ad2e47d5134b06ef0bb73.json";

	private static final byte[] BYTE_ARRAY = hexStringToByteArray(TEST_ADDRESS);

	private static final BigInteger TEST_BIG_INT = BigInteger.valueOf(1364961235);

	private static byte[] bytesArray;

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	// convertToType

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
		inputParam.put(VALUE, BYTE_ARRAY);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the DynamicBytes correctly.", result, new DynamicBytes(BYTE_ARRAY));
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
		EthereumPipFunctions.convertToType(inputParam);
	}

	@Test
	public void convertToTypeShouldReturnDoubleTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "double");
		inputParam.put(VALUE, Double.valueOf(1.789));
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Double correctly.", result,
				new org.web3j.abi.datatypes.primitive.Double(1.789));
	}

	@Test
	public void convertToTypeShouldReturnFloatTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "float");
		inputParam.put(VALUE, Float.valueOf("7.654321"));
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Float correctly.", result,
				new org.web3j.abi.datatypes.primitive.Float(Float.valueOf("7.654321")));
	}

	@Test
	public void convertToTypeShouldReturnUintTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint");
		inputParam.put(VALUE, TEST_BIG_INT);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint correctly.", result, new Uint(TEST_BIG_INT));
	}

	@Test
	public void convertToTypeShouldReturnIntTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int");
		inputParam.put(VALUE, TEST_BIG_INT);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int correctly.", result, new Int(TEST_BIG_INT));
	}

	@Test
	public void convertToTypeShouldReturnLongTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "long");
		inputParam.put(VALUE, Long.valueOf(9786135));
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Long correctly.", result,
				new org.web3j.abi.datatypes.primitive.Long(Long.valueOf(9786135)));
	}

	@Test
	public void convertToTypeShouldReturnShortTypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "short");
		inputParam.put(VALUE, Short.valueOf("111"));
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Short correctly.", result,
				new org.web3j.abi.datatypes.primitive.Short(Short.valueOf("111")));
	}

	@Test
	public void convertToTypeShouldReturnUint8TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint8");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint8 correctly.", result, new Uint8(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt8TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int8");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int8 correctly.", result, new Int8(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint16TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint16");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint16 correctly.", result, new Uint16(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt16TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int16");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int16 correctly.", result, new Int16(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint24TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint24");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint24 correctly.", result, new Uint24(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt24TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int24");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int24 correctly.", result, new Int24(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint32TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint32");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint32 correctly.", result, new Uint32(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt32TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int32");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int32 correctly.", result, new Int32(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint40TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint40");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint40 correctly.", result, new Uint40(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt40TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int40");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int40 correctly.", result, new Int40(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint48TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint48");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint48 correctly.", result, new Uint48(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt48TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int48");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int48 correctly.", result, new Int48(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint56TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint56");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint56 correctly.", result, new Uint56(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt56TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int56");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int56 correctly.", result, new Int56(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint64TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint64");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint64 correctly.", result, new Uint64(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt64TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int64");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int64 correctly.", result, new Int64(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint72TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint72");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint72 correctly.", result, new Uint72(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt72TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int72");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int72 correctly.", result, new Int72(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint80TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint80");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint80 correctly.", result, new Uint80(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt80TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int80");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int80 correctly.", result, new Int80(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint88TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint88");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint88 correctly.", result, new Uint88(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt88TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int88");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int88 correctly.", result, new Int88(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint96TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint96");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint96 correctly.", result, new Uint96(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt96TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int96");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int96 correctly.", result, new Int96(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint104TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint104");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint104 correctly.", result, new Uint104(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt104TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int104");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int104 correctly.", result, new Int104(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint112TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint112");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint112 correctly.", result, new Uint112(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt112TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int112");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int112 correctly.", result, new Int112(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint120TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint120");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint120 correctly.", result, new Uint120(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt120TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int120");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int120 correctly.", result, new Int120(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint128TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint128");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint128 correctly.", result, new Uint128(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt128TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int128");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int128 correctly.", result, new Int128(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint136TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint136");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint136 correctly.", result, new Uint136(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt136TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int136");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int136 correctly.", result, new Int136(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint144TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint144");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint144 correctly.", result, new Uint144(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt144TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int144");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int144 correctly.", result, new Int144(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint152TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint152");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint152 correctly.", result, new Uint152(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt152TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int152");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int152 correctly.", result, new Int152(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint160TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint160");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint160 correctly.", result, new Uint160(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt160TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int160");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int160 correctly.", result, new Int160(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint168TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint168");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint168 correctly.", result, new Uint168(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt168TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int168");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int168 correctly.", result, new Int168(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint176TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint176");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint176 correctly.", result, new Uint176(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt176TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int176");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int176 correctly.", result, new Int176(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint184TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint184");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint184 correctly.", result, new Uint184(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt184TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int184");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int184 correctly.", result, new Int184(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint192TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint192");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint192 correctly.", result, new Uint192(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt192TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int192");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int192 correctly.", result, new Int192(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint200TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint200");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint200 correctly.", result, new Uint200(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt200TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int200");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int200 correctly.", result, new Int200(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint208TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint208");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint208 correctly.", result, new Uint208(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt208TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int208");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int208 correctly.", result, new Int208(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint216TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint216");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint216 correctly.", result, new Uint216(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt216TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int216");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int216 correctly.", result, new Int216(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint224TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint224");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint224 correctly.", result, new Uint224(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt224TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int224");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int224 correctly.", result, new Int224(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint232TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint232");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint232 correctly.", result, new Uint232(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt232TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int232");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int232 correctly.", result, new Int232(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint240TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint240");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint240 correctly.", result, new Uint240(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt240TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int240");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int240 correctly.", result, new Int240(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint248TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint248");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint248 correctly.", result, new Uint248(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt248TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int248");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int248 correctly.", result, new Int248(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnUint256TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "uint256");
		inputParam.put(VALUE, UINT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Uint256 correctly.", result, new Uint256(UINT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnInt256TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "int256");
		inputParam.put(VALUE, INT_TEST_VALUE);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Int256 correctly.", result, new Int256(INT_TEST_VALUE));
	}

	@Test
	public void convertToTypeShouldReturnBytes1TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[1];
		bytesArray[0] = 25;
		inputParam.put(TYPE, "bytes1");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes1 correctly.", result, new Bytes1(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes2TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[2];
		bytesArray[1] = 33;
		inputParam.put(TYPE, "bytes2");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes2 correctly.", result, new Bytes2(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes3TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[3];
		inputParam.put(TYPE, "bytes3");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes3 correctly.", result, new Bytes3(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes4TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[4];
		inputParam.put(TYPE, "bytes4");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes4 correctly.", result, new Bytes4(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes5TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[5];
		inputParam.put(TYPE, "bytes5");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes5 correctly.", result, new Bytes5(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes6TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[6];
		inputParam.put(TYPE, "bytes6");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes6 correctly.", result, new Bytes6(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes7TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[7];
		inputParam.put(TYPE, "bytes7");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes7 correctly.", result, new Bytes7(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes8TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[8];
		inputParam.put(TYPE, "bytes8");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes8 correctly.", result, new Bytes8(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes9TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[9];
		inputParam.put(TYPE, "bytes9");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes9 correctly.", result, new Bytes9(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes10TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[10];
		inputParam.put(TYPE, "bytes10");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes10 correctly.", result, new Bytes10(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnByte11TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[11];
		inputParam.put(TYPE, "bytes11");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes11 correctly.", result, new Bytes11(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes12TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[12];
		inputParam.put(TYPE, "bytes12");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes12 correctly.", result, new Bytes12(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes13TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[13];
		inputParam.put(TYPE, "bytes13");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes13 correctly.", result, new Bytes13(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes14TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[14];
		inputParam.put(TYPE, "bytes14");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes14 correctly.", result, new Bytes14(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes15TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[15];
		inputParam.put(TYPE, "bytes15");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes15 correctly.", result, new Bytes15(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes16TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[16];
		inputParam.put(TYPE, "bytes16");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes16 correctly.", result, new Bytes16(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes17TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[17];
		inputParam.put(TYPE, "bytes17");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes17 correctly.", result, new Bytes17(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes18TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[18];
		inputParam.put(TYPE, "bytes18");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes18 correctly.", result, new Bytes18(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes19TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[19];
		inputParam.put(TYPE, "bytes19");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes19 correctly.", result, new Bytes19(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes20TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[20];
		inputParam.put(TYPE, "bytes20");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes20 correctly.", result, new Bytes20(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes21TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[21];
		inputParam.put(TYPE, "bytes21");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes21 correctly.", result, new Bytes21(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnByte22TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[22];
		inputParam.put(TYPE, "bytes22");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes22 correctly.", result, new Bytes22(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes23TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[23];
		inputParam.put(TYPE, "bytes23");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes23 correctly.", result, new Bytes23(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes24TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[24];
		inputParam.put(TYPE, "bytes24");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes24 correctly.", result, new Bytes24(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes25TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[25];
		inputParam.put(TYPE, "bytes25");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes25 correctly.", result, new Bytes25(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes26TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[26];
		inputParam.put(TYPE, "bytes26");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes26 correctly.", result, new Bytes26(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes27TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[27];
		inputParam.put(TYPE, "bytes27");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes27 correctly.", result, new Bytes27(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes28TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[28];
		inputParam.put(TYPE, "bytes28");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes28 correctly.", result, new Bytes28(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes29TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[29];
		inputParam.put(TYPE, "bytes29");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes29 correctly.", result, new Bytes29(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes30TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[30];
		inputParam.put(TYPE, "bytes30");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes30 correctly.", result, new Bytes30(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes31TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[31];
		inputParam.put(TYPE, "bytes31");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes31 correctly.", result, new Bytes31(bytesArray));
	}

	@Test
	public void convertToTypeShouldReturnBytes32TypeCorrectly() throws IOException, AttributeException {
		ObjectNode inputParam = JSON.objectNode();
		bytesArray = new byte[32];
		inputParam.put(TYPE, "bytes32");
		inputParam.put(VALUE, bytesArray);
		Type result = EthereumPipFunctions.convertToType(inputParam);
		assertEquals("ConvertToType didn't return the Bytes32 correctly.", result, new Bytes32(bytesArray));
	}

	@Test(expected = AttributeException.class)
	public void falseSolidityTypeShouldThrowAttributeException() throws AttributeException, IOException {
		ObjectNode inputParam = JSON.objectNode();
		inputParam.put(TYPE, "wrongType");
		inputParam.put(VALUE, "anyValue");
		EthereumPipFunctions.convertToType(inputParam);
	}

	// loadCredentials
	@Test
	public void loadCredentialsShouldWorkWithCredentialsFromPolicy()
			throws AttributeException, IOException, CipherException {
		ObjectNode inputParam = JSON.objectNode();
		ObjectNode wallet = JSON.objectNode();
		wallet.put(WALLET_FILE, KEYSTORE + USER1WALLET);
		wallet.put(WALLET_PASS, "");
		inputParam.set(ETHEREUM_WALLET, wallet);
		Credentials credentials = EthereumPipFunctions.loadCredentials(inputParam, null);
		assertEquals("Credentials couldn't be loaded from saplObject.", credentials,
				WalletUtils.loadCredentials("", KEYSTORE + USER1WALLET));
		assertEquals("load Credentials didn't load an object of Credentials class.", credentials.getClass(),
				Credentials.class);
	}

	@Test
	public void loadCredentialsShouldWorkWithCredentialsFromVariables()
			throws AttributeException, IOException, CipherException {
		ObjectNode wallet = JSON.objectNode();
		wallet.put(WALLET_FILE, KEYSTORE + USER1WALLET);
		wallet.put(WALLET_PASS, "");
		Map<String, JsonNode> inputVariables = new HashMap<String, JsonNode>();
		inputVariables.put(ETHEREUM_WALLET, wallet);
		Credentials credentials = EthereumPipFunctions.loadCredentials(JSON.nullNode(), inputVariables);
		assertEquals("Credentials couldn't be loaded from saplObject.", credentials,
				WalletUtils.loadCredentials("", KEYSTORE + USER1WALLET));
		assertEquals("load Credentials didn't load an object of Credentials class.", credentials.getClass(),
				Credentials.class);
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
