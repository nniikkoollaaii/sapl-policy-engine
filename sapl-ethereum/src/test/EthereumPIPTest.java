package io.sapl.interpreter.pip


public class EthereumPIPTest {
    
    private Web3j web3j;
    
    @Before
    public void init() {
	Web3j web3j = new HttpService();
	EthereumPolicyInformationPoint ethPip = new EthereumPolicyInformationPoint(web3j);
    }
    
    @Test
    public void verifyTransactionShouldReturnTrueWithCorrectTransaction() {
	
	
    }

}
