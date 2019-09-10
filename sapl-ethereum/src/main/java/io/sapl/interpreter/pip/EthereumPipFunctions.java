package io.sapl.interpreter.pip;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class EthereumPipFunctions {

    private static final Logger logger = LoggerFactory.getLogger(EthereumPipFunctions.class);

    private static final String ETHEREUM_WALLET = "ethereumWallet";

    private static final String WALLET_PASSWORD = "walletPassword";

    private static final String WALLET_FILE = "walletFile";

    private static final String NO_CREDENTIALS_WARNING = "Could not load Credentials. Please ensure that your "
	    + "credentials are annotated correctly either in the policy or in the pdp.json file.";

    private static final String CREDENTIALS_LOADING_ERROR = ETHEREUM_WALLET + " has been found, but the credentials "
	    + "couldn't be retrieved. Please ensure your Password and Wallet File Path were correct.";

    public static Credentials loadCredentials(JsonNode saplObject, Map<String, JsonNode> variables) {

	// First trying to load Credentials that only apply with the given policy.
	if (saplObject.has(ETHEREUM_WALLET)) {
	    return retrieveCredentials(saplObject.get(ETHEREUM_WALLET));
	}

	// If no credentials where found in policy, they will be loaded from the
	// pdp.json
	if (variables.containsKey(ETHEREUM_WALLET)) {
	    return retrieveCredentials(variables.get(ETHEREUM_WALLET));
	}

	logger.warn(NO_CREDENTIALS_WARNING);
	return null;
    }

    private static Credentials retrieveCredentials(JsonNode ethereumWallet) {
	if (ethereumWallet.has(WALLET_PASSWORD) && ethereumWallet.has(WALLET_FILE)) {
	    String walletPassword = ethereumWallet.get(WALLET_PASSWORD).textValue();
	    String walletFile = ethereumWallet.get(WALLET_FILE).textValue();
	    try {
		Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletFile);
		return credentials;
	    } catch (IOException | CipherException e) {
		logger.warn(CREDENTIALS_LOADING_ERROR);
	    }
	}

	logger.warn(NO_CREDENTIALS_WARNING);
	return null;

    }

//    public static Type convertToType(JsonNode inputParam) {
//	String solidityType = inputParam.get("type").textValue();
//	JsonNode value = inputParam.get("value");
//
//	switch (solidityType) {
//	case "address":
//	    return new Address(value.textValue());
//	case "bool":
//	case "boolean":
//	    return new Bool(value.asBoolean());
//	case "string":
//	    return new Utf8String(value.textValue());
//	case "bytes":
//	    return new DynamicBytes(value.binaryValue());
//	case "byte":
//	    return new Byte(new java.lang.Byte(value.textValue()));
//	case "char":
//	    return Char.class;
//	case "double":
//	    return Double.class;
//	case "float":
//	    return Float.class;
//	case "uint":
//	    return Uint.class;
//	case "int":
//	    return primitives ? Int.class : org.web3j.abi.datatypes.Int.class;
//	case "long":
//	    return Long.class;
//	case "short":
//	    return Short.class;
//	case "uint8":
//	    return Uint8.class;
//	case "int8":
//	    return primitives ? Short.class : Int8.class;
//	case "uint16":
//	    return primitives ? Int.class : Uint16.class;
//	case "int16":
//	    return primitives ? Int.class : Int16.class;
//	case "uint24":
//	    return primitives ? Int.class : Uint24.class;
//	case "int24":
//	    return primitives ? Int.class : Int24.class;
//	case "uint32":
//	    return primitives ? Long.class : Uint32.class;
//	case "int32":
//	    return primitives ? Int.class : Int32.class;
//	case "uint40":
//	    return primitives ? Long.class : Int40.class;
//	case "int40":
//	    return primitives ? Long.class : Uint40.class;
//	case "uint48":
//	    return primitives ? Long.class : Uint48.class;
//	case "int48":
//	    return primitives ? Long.class : Int48.class;
//	case "uint56":
//	    return primitives ? Long.class : Uint56.class;
//	case "int56":
//	    return primitives ? Long.class : Int56.class;
//	case "uint64":
//	    return Uint64.class;
//	case "int64":
//	    return primitives ? Long.class : Int64.class;
//	case "uint72":
//	    return Uint72.class;
//	case "int72":
//	    return Int72.class;
//	case "uint80":
//	    return Uint80.class;
//	case "int80":
//	    return Int80.class;
//	case "uint88":
//	    return Uint88.class;
//	case "int88":
//	    return Int88.class;
//	case "uint96":
//	    return Uint96.class;
//	case "int96":
//	    return Int96.class;
//	case "uint104":
//	    return Uint104.class;
//	case "int104":
//	    return Int104.class;
//	case "uint112":
//	    return Uint112.class;
//	case "int112":
//	    return Int112.class;
//	case "uint120":
//	    return Uint120.class;
//	case "int120":
//	    return Int120.class;
//	case "uint128":
//	    return Uint128.class;
//	case "int128":
//	    return Int128.class;
//	case "uint136":
//	    return Uint136.class;
//	case "int136":
//	    return Int136.class;
//	case "uint144":
//	    return Uint144.class;
//	case "int144":
//	    return Int144.class;
//	case "uint152":
//	    return Uint152.class;
//	case "int152":
//	    return Int152.class;
//	case "uint160":
//	    return Uint160.class;
//	case "int160":
//	    return Int160.class;
//	case "uint168":
//	    return Uint168.class;
//	case "int168":
//	    return Int168.class;
//	case "uint176":
//	    return Uint176.class;
//	case "int176":
//	    return Int176.class;
//	case "uint184":
//	    return Uint184.class;
//	case "int184":
//	    return Int184.class;
//	case "uint192":
//	    return Uint192.class;
//	case "int192":
//	    return Int192.class;
//	case "uint200":
//	    return Uint200.class;
//	case "int200":
//	    return Int200.class;
//	case "uint208":
//	    return Uint208.class;
//	case "int208":
//	    return Int208.class;
//	case "uint216":
//	    return Uint216.class;
//	case "int216":
//	    return Int216.class;
//	case "uint224":
//	    return Uint224.class;
//	case "int224":
//	    return Int224.class;
//	case "uint232":
//	    return Uint232.class;
//	case "int232":
//	    return Int232.class;
//	case "uint240":
//	    return Uint240.class;
//	case "int240":
//	    return Int240.class;
//	case "uint248":
//	    return Uint248.class;
//	case "int248":
//	    return Int248.class;
//	case "uint256":
//	    return Uint256.class;
//	case "int256":
//	    return Int256.class;
//	case "bytes1":
//	    return Bytes1.class;
//	case "bytes2":
//	    return Bytes2.class;
//	case "bytes3":
//	    return Bytes3.class;
//	case "bytes4":
//	    return Bytes4.class;
//	case "bytes5":
//	    return Bytes5.class;
//	case "bytes6":
//	    return Bytes6.class;
//	case "bytes7":
//	    return Bytes7.class;
//	case "bytes8":
//	    return Bytes8.class;
//	case "bytes9":
//	    return Bytes9.class;
//	case "bytes10":
//	    return Bytes10.class;
//	case "bytes11":
//	    return Bytes11.class;
//	case "bytes12":
//	    return Bytes12.class;
//	case "bytes13":
//	    return Bytes13.class;
//	case "bytes14":
//	    return Bytes14.class;
//	case "bytes15":
//	    return Bytes15.class;
//	case "bytes16":
//	    return Bytes16.class;
//	case "bytes17":
//	    return Bytes17.class;
//	case "bytes18":
//	    return Bytes18.class;
//	case "bytes19":
//	    return Bytes19.class;
//	case "bytes20":
//	    return Bytes20.class;
//	case "bytes21":
//	    return Bytes21.class;
//	case "bytes22":
//	    return Bytes22.class;
//	case "bytes23":
//	    return Bytes23.class;
//	case "bytes24":
//	    return Bytes24.class;
//	case "bytes25":
//	    return Bytes25.class;
//	case "bytes26":
//	    return Bytes26.class;
//	case "bytes27":
//	    return Bytes27.class;
//	case "bytes28":
//	    return Bytes28.class;
//	case "bytes29":
//	    return Bytes29.class;
//	case "bytes30":
//	    return Bytes30.class;
//	case "bytes31":
//	    return Bytes31.class;
//	case "bytes32":
//	    return Bytes32.class;
//	default:
//	    throw new UnsupportedOperationException("Unsupported type encountered: " + type);
//	}
//    }

}
