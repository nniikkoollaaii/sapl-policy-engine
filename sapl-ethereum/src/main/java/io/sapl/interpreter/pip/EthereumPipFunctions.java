package io.sapl.interpreter.pip;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
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
import org.web3j.protocol.core.DefaultBlockParameter;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pip.AttributeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthereumPipFunctions {

	private static final String VALUE = "value";

	private static final String TYPE = "type";

	private static final String ETHEREUM_WALLET = "ethereumWallet";

	private static final String WALLET_PASS = "walletPassword";

	private static final String WALLET_FILE = "walletFile";

	private static final String NO_CREDENTIALS_WARNING = "Could not load Credentials. Please ensure that your "
			+ "credentials are annotated correctly either in the policy or in the pdp.json file.";

	private static final String CREDENTIALS_LOADING_ERROR = ETHEREUM_WALLET + " has been found, but the credentials "
			+ "couldn't be retrieved. Please ensure your Password and Wallet File Path were correct.";

	private static final String DEFAULT_BLOCK_PARAMETER_BIG_INT = "defaultBlockParameterBigInt";

	private static final String DEFAULT_BLOCK_PARAMETER_STRING = "defaultBlockParameterString";

	private static final String LATEST = "latest";

	private static final String EARLIEST = "earliest";

	private static final String PENDING = "pending";

	private static final String NO_DBP_WARNING = "The DefaultBlockParameter was not correctly provided. By default the latest Block is used.";

	private EthereumPipFunctions() {

	}

	/**
	 * Determines the DefaultBlockParameter needed for some Ethereum API calls. This Parameter can be a BigInteger
	 * number or one of the Strings "latest", "earliest" or "pending". If the DefaultBlockParameter is not provided in
	 * the policy, the latest Block is used. In this case there is also a warning.
	 *
	 * @param saplObject should hold one of the following values: <br>
	 * "defaultBlockParameterBigInt": BigInteger value of the desired block number. <br>
	 * <b>or</b> <br>
	 * "defaultBlockParameterString": Holding one of the strings "latest", "earliest", or "pending".
	 * @return The DefaultBlockParameter corresponding to the input.
	 */
	protected static DefaultBlockParameter extractDefaultBlockParameter(JsonNode saplObject) {
		if (saplObject.has(DEFAULT_BLOCK_PARAMETER_BIG_INT)) {
			JsonNode dbp = saplObject.get(DEFAULT_BLOCK_PARAMETER_BIG_INT);
			BigInteger dbpValue = dbp.bigIntegerValue();
			return DefaultBlockParameter.valueOf(dbpValue);
		}
		if (saplObject.has(DEFAULT_BLOCK_PARAMETER_STRING)) {
			String dbpsName = saplObject.get(DEFAULT_BLOCK_PARAMETER_STRING).textValue();
			if (dbpsName.equals(EARLIEST) || dbpsName.equals(LATEST) || dbpsName.equals(PENDING))
				return DefaultBlockParameter.valueOf(dbpsName);
		}

		LOGGER.warn(NO_DBP_WARNING);
		return DefaultBlockParameter.valueOf(LATEST);

	}

	public static Credentials loadCredentials(JsonNode saplObject, Map<String, JsonNode> variables)
			throws AttributeException {

		// First trying to load Credentials that only apply with the given policy.
		if (saplObject.has(ETHEREUM_WALLET)) {
			return retrieveCredentials(saplObject.get(ETHEREUM_WALLET));
		}

		// If no credentials where found in policy, they will be loaded from the
		// pdp.json
		if (variables.containsKey(ETHEREUM_WALLET)) {
			return retrieveCredentials(variables.get(ETHEREUM_WALLET));
		}

		throw new AttributeException(NO_CREDENTIALS_WARNING);
	}

	private static Credentials retrieveCredentials(JsonNode ethereumWallet) throws AttributeException {
		if (ethereumWallet.has(WALLET_PASS) && ethereumWallet.has(WALLET_FILE)) {
			String walletPassword = ethereumWallet.get(WALLET_PASS).textValue();
			String walletFile = ethereumWallet.get(WALLET_FILE).textValue();
			try {
				return WalletUtils.loadCredentials(walletPassword, walletFile);
			}
			catch (IOException | CipherException e) {
				throw new AttributeException(CREDENTIALS_LOADING_ERROR);
			}
		}

		throw new AttributeException(NO_CREDENTIALS_WARNING);

	}

	public static Type convertToType(JsonNode inputParam) throws AttributeException {
		if (inputParam == null) {
			throw new AttributeException("An input Parameter for convertToType was null");
		}

		if (inputParam.has(TYPE) && inputParam.has(VALUE)) {
			String solidityType = inputParam.get(TYPE).textValue();
			JsonNode value = inputParam.get(VALUE);
			String textValue = value.textValue();
			BigInteger bigIntegerValue = value.bigIntegerValue();
			byte[] binaryValue = new byte[0];
			if (value.isBinary()) {
				try {
					binaryValue = value.binaryValue();
				}
				catch (IOException e) {
					throw new AttributeException(
							"Error in convertToType while converting " + value + " to binary value.", e);
				}
			}

			switch (solidityType) {
			case "address":
				return new Address(textValue);
			case "bool":
			case "boolean":
				return new Bool(value.asBoolean());
			case "string":
				return new Utf8String(textValue);
			case "bytes":
				return new DynamicBytes(binaryValue);
			case "byte":
				return new org.web3j.abi.datatypes.primitive.Byte((byte) value.asInt());
			case "char":
				if (textValue.isEmpty()) {
					throw new AttributeException("Expected a String with at least one char but got empty String.");
				}
				return new Char(textValue.charAt(0));
			case "double":
				return new org.web3j.abi.datatypes.primitive.Double(value.asDouble());
			case "float":
				return new org.web3j.abi.datatypes.primitive.Float(value.floatValue());
			case "uint":
				return new Uint(bigIntegerValue);
			case "int":
				return new org.web3j.abi.datatypes.Int(bigIntegerValue);
			case "long":
				return new org.web3j.abi.datatypes.primitive.Long(value.asLong());
			case "short":
				return new org.web3j.abi.datatypes.primitive.Short(value.shortValue());
			case "uint8":
				return new Uint8(bigIntegerValue);
			case "int8":
				return new Int8(bigIntegerValue);
			case "uint16":
				return new Uint16(bigIntegerValue);
			case "int16":
				return new Int16(bigIntegerValue);
			case "uint24":
				return new Uint24(bigIntegerValue);
			case "int24":
				return new Int24(bigIntegerValue);
			case "uint32":
				return new Uint32(bigIntegerValue);
			case "int32":
				return new Int32(bigIntegerValue);
			case "uint40":
				return new Uint40(bigIntegerValue);
			case "int40":
				return new Int40(bigIntegerValue);
			case "uint48":
				return new Uint48(bigIntegerValue);
			case "int48":
				return new Int48(bigIntegerValue);
			case "uint56":
				return new Uint56(bigIntegerValue);
			case "int56":
				return new Int56(bigIntegerValue);
			case "uint64":
				return new Uint64(bigIntegerValue);
			case "int64":
				return new Int64(bigIntegerValue);
			case "uint72":
				return new Uint72(bigIntegerValue);
			case "int72":
				return new Int72(bigIntegerValue);
			case "uint80":
				return new Uint80(bigIntegerValue);
			case "int80":
				return new Int80(bigIntegerValue);
			case "uint88":
				return new Uint88(bigIntegerValue);
			case "int88":
				return new Int88(bigIntegerValue);
			case "uint96":
				return new Uint96(bigIntegerValue);
			case "int96":
				return new Int96(bigIntegerValue);
			case "uint104":
				return new Uint104(bigIntegerValue);
			case "int104":
				return new Int104(bigIntegerValue);
			case "uint112":
				return new Uint112(bigIntegerValue);
			case "int112":
				return new Int112(bigIntegerValue);
			case "uint120":
				return new Uint120(bigIntegerValue);
			case "int120":
				return new Int120(bigIntegerValue);
			case "uint128":
				return new Uint128(bigIntegerValue);
			case "int128":
				return new Int128(bigIntegerValue);
			case "uint136":
				return new Uint136(bigIntegerValue);
			case "int136":
				return new Int136(bigIntegerValue);
			case "uint144":
				return new Uint144(bigIntegerValue);
			case "int144":
				return new Int144(bigIntegerValue);
			case "uint152":
				return new Uint152(bigIntegerValue);
			case "int152":
				return new Int152(bigIntegerValue);
			case "uint160":
				return new Uint160(bigIntegerValue);
			case "int160":
				return new Int160(bigIntegerValue);
			case "uint168":
				return new Uint168(bigIntegerValue);
			case "int168":
				return new Int168(bigIntegerValue);
			case "uint176":
				return new Uint176(bigIntegerValue);
			case "int176":
				return new Int176(bigIntegerValue);
			case "uint184":
				return new Uint184(bigIntegerValue);
			case "int184":
				return new Int184(bigIntegerValue);
			case "uint192":
				return new Uint192(bigIntegerValue);
			case "int192":
				return new Int192(bigIntegerValue);
			case "uint200":
				return new Uint200(bigIntegerValue);
			case "int200":
				return new Int200(bigIntegerValue);
			case "uint208":
				return new Uint208(bigIntegerValue);
			case "int208":
				return new Int208(bigIntegerValue);
			case "uint216":
				return new Uint216(bigIntegerValue);
			case "int216":
				return new Int216(bigIntegerValue);
			case "uint224":
				return new Uint224(bigIntegerValue);
			case "int224":
				return new Int224(bigIntegerValue);
			case "uint232":
				return new Uint232(bigIntegerValue);
			case "int232":
				return new Int232(bigIntegerValue);
			case "uint240":
				return new Uint240(bigIntegerValue);
			case "int240":
				return new Int240(bigIntegerValue);
			case "uint248":
				return new Uint248(bigIntegerValue);
			case "int248":
				return new Int248(bigIntegerValue);
			case "uint256":
				return new Uint256(bigIntegerValue);
			case "int256":
				return new Int256(bigIntegerValue);
			case "bytes1":
				return new Bytes1(binaryValue);
			case "bytes2":
				return new Bytes2(binaryValue);
			case "bytes3":
				return new Bytes3(binaryValue);
			case "bytes4":
				return new Bytes4(binaryValue);
			case "bytes5":
				return new Bytes5(binaryValue);
			case "bytes6":
				return new Bytes6(binaryValue);
			case "bytes7":
				return new Bytes7(binaryValue);
			case "bytes8":
				return new Bytes8(binaryValue);
			case "bytes9":
				return new Bytes9(binaryValue);
			case "bytes10":
				return new Bytes10(binaryValue);
			case "bytes11":
				return new Bytes11(binaryValue);
			case "bytes12":
				return new Bytes12(binaryValue);
			case "bytes13":
				return new Bytes13(binaryValue);
			case "bytes14":
				return new Bytes14(binaryValue);
			case "bytes15":
				return new Bytes15(binaryValue);
			case "bytes16":
				return new Bytes16(binaryValue);
			case "bytes17":
				return new Bytes17(binaryValue);
			case "bytes18":
				return new Bytes18(binaryValue);
			case "bytes19":
				return new Bytes19(binaryValue);
			case "bytes20":
				return new Bytes20(binaryValue);
			case "bytes21":
				return new Bytes21(binaryValue);
			case "bytes22":
				return new Bytes22(binaryValue);
			case "bytes23":
				return new Bytes23(binaryValue);
			case "bytes24":
				return new Bytes24(binaryValue);
			case "bytes25":
				return new Bytes25(binaryValue);
			case "bytes26":
				return new Bytes26(binaryValue);
			case "bytes27":
				return new Bytes27(binaryValue);
			case "bytes28":
				return new Bytes28(binaryValue);
			case "bytes29":
				return new Bytes29(binaryValue);
			case "bytes30":
				return new Bytes30(binaryValue);
			case "bytes31":
				return new Bytes31(binaryValue);
			case "bytes32":
				return new Bytes32(binaryValue);
			default:
				throw new AttributeException("The type name " + solidityType + " could not be recognized.");
			}

		}
		throw new AttributeException("An input parameter for the Ethereum function call didn't have the fields '" + TYPE
				+ "' and '" + VALUE + "'.");
	}

}
