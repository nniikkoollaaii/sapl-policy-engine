package io.sapl.interpreter.pip.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.0.
 */
public class Certification extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610588806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063990b99a71461003b578063bb721aab1461010e575b600080fd5b6100fa6004803603606081101561005157600080fd5b6001600160a01b03823581169260208101359091169181019060608101604082013564010000000081111561008557600080fd5b82018360208201111561009757600080fd5b803590602001918460018302840111640100000000831117156100b957600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506101c6945050505050565b604080519115158252519081900360200190f35b6101c46004803603604081101561012457600080fd5b6001600160a01b03823516919081019060408101602082013564010000000081111561014f57600080fd5b82018360208201111561016157600080fd5b8035906020019184600183028401116401000000008311171561018357600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061035a945050505050565b005b6001600160a01b0383166000908152602081815260408083208054825181850281018501909352808352606093859084015b828210156102cc576000848152602090819020604080518082018252600286810290930180546001600160a01b031682526001808201805485516101009382161593909302600019011695909504601f8101879004870282018701909452838152919490938582019390918301828280156102b45780601f10610289576101008083540402835291602001916102b4565b820191906000526020600020905b81548152906001019060200180831161029757829003601f168201915b505050505081525050815260200190600101906101f8565b509293506000925050505b815181101561034c57846001600160a01b03168282815181106102f657fe5b6020026020010151600001516001600160a01b0316148015610334575061033482828151811061032257fe5b602002602001015160200151856103d1565b1561034457600192505050610353565b6001016102d7565b5060009150505b9392505050565b6001600160a01b0382811660009081526020818152604080832081518083019092523382528183018681528154600180820180855593875295859020845160029092020180546001600160a01b031916919097161786555180519195929492936103c9938501929101906104b8565b505050505050565b6000816040516020018082805190602001908083835b602083106104065780518252601f1990920191602091820191016103e7565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405160208183030381529060405280519060200120836040516020018082805190602001908083835b602083106104745780518252601f199092019160209182019101610455565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040528051906020012014905092915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106104f957805160ff1916838001178555610526565b82800160010185558215610526579182015b8281111561052657825182559160200191906001019061050b565b50610532929150610536565b5090565b61055091905b80821115610532576000815560010161053c565b9056fea265627a7a723058201b6f9ca6070517bbc5abb296ec8dd82ffec5756987b3507c247a32c268ac473b64736f6c63430005090032";

    public static final String FUNC_HASCERTIFICATE = "hasCertificate";

    public static final String FUNC_ISSUECERTIFICATE = "issueCertificate";

    @Deprecated
    protected Certification(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Certification(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Certification(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Certification(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Boolean> hasCertificate(String graduate, String certificateIssuer, String certificateName) {
        final Function function = new Function(FUNC_HASCERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, graduate), 
                new org.web3j.abi.datatypes.Address(160, certificateIssuer), 
                new org.web3j.abi.datatypes.Utf8String(certificateName)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> issueCertificate(String graduate, String certificateName) {
        final Function function = new Function(
                FUNC_ISSUECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, graduate), 
                new org.web3j.abi.datatypes.Utf8String(certificateName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Certification load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Certification(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Certification load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Certification(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Certification load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Certification(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Certification load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Certification(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Certification> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Certification.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Certification> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Certification.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Certification> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Certification.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Certification> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Certification.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
