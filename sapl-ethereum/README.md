# Sapl-Ethereum Documentation

## loadContractInformation

This function was added to provide a simple, user-friendly way of retreiving information from a contract on the Ethereum Blockchain. It needs to receive a JsonNode with the following information:

 - "fromAccount":  (Optional) The account which the request is send from
 - "contractAddress":    The address of the contract that should be called
 - "functionName": The name of the function that should be called
 - "inputParams":  The type and value of all input params that the called function requires in the same order as in the function declaration
 - "outputParams": The type of all output params that the function returns in the same order as in the function declaration.

All types that can be used are listed in the convertToType-method of the [EthereumPipFunctions](https://github.com/heutelbeck/sapl-policy-engine/blob/sapl-ethereum/sapl-ethereum/src/main/java/io/sapl/interpreter/pip/EthereumPipFunctions.java).

For examples of how to use the types with correct values you can have a look at the [EthereumPipFunctionsTest](https://github.com/heutelbeck/sapl-policy-engine/blob/sapl-ethereum/sapl-ethereum/src/test/java/io/sapl/interpreter/pip/EthereumPipFunctionsTest.java).

 
Let's assume that you want to call the function `hasCertificate` from the following contract:

```solidity
contract Device_Operator_Certificate {

  // The certification authority decides who can issue a certificate
  address public certificationAuthority;

  string public certificateName = "Device_Operator_Certificate";

  uint public timeValid = 365 days;

  struct Certificate {
    bool obtained;
    address issuer;
    uint issueTime;
  }

  // contains true for addresses that are authorized to issue a certificate
  mapping (address => bool) authorizedIssuers;

  // contains all certificates that have been issued
  mapping (address => Certificate) certificateHolders;

  // The creator of the contract is also the certification authority
  constructor() public {
    certificationAuthority = msg.sender;
  }

  function issueCertificate (address graduate) public {
    require(
      authorizedIssuers[msg.sender],
      "Only the authorized issuers can issue certificates."
    );

    certificateHolders[graduate].obtained = true;
    certificateHolders[graduate].issuer = msg.sender;
    // The issue time is the timestamp of the block which contains the
    // transaction that actually issues the certificate
    certificateHolders[graduate].issueTime = block.timestamp;
  }

  function revokeCertificate (address graduate) public {
    require(
      certificateHolders[graduate].issuer == msg.sender,
      "Only the issuer can revoke the certificate."
      );
    certificateHolders[graduate].obtained = false;
  }


  function hasCertificate(address graduate) public view
          returns (bool certificateOwned) {
    // verifies if the certificate is still valid
    // here block.timestamp refers to the timestamp of the block the request
    // is made to (usually the latest)
    if (block.timestamp < certificateHolders[graduate].issueTime + timeValid) {
      return certificateHolders[graduate].obtained;
    }
    return false;
  }

  function addIssuer (address newIssuer) public {
    require(
      msg.sender == certificationAuthority,
      "Only the Certification Authority can name new certificate issuers."
      );
    authorizedIssuers[newIssuer] = true;
  }

  function removeIssuer (address issuerToRemove) public {
    require(
      msg.sender == certificationAuthority,
      "Only the Certification Authority can remove certificate issuers."
      );
    authorizedIssuers[issuerToRemove] = false;
  }

}
```

The contract has been published to the address `0x2d53b58c67ba813c2d1962f8a712ef5533c07c59`.
Furthermore, you want to know if the Ethereum user with the address `3f2cbea2185089ea5bbabbcd7616b215b724885c` has a valid certificate.
In this case your JsonNode should look like that:


```json
{
	"contractAddress":"0x2d53b58c67ba813c2d1962f8a712ef5533c07c59",
	"functionName":"hasCertificate",
	"inputParams":[{"type":"address","value":"3f2cbea2185089ea5bbabbcd7616b215b724885c"}],
	"outputParams":["bool"]
}
```

The result will be an ArrayNode with an entry tuple for each returned value. 
Example with one return value of type boolean:

```json
[{"value":true,"typeAsString":"bool"}]
```

Using this in your Application you could have a policy set like this one:

```
set "ethereumPolicies"
deny-unless-permit
//for subject.contractAddress == "0x2d53b58c67ba813c2d1962f8a712ef5533c07c59"
//var certificate_contract = "0x2d53b58c67ba813c2d1962f8a712ef5533c07c59";


policy "test_eth_policy"
permit
  action=="operate" & resource=="device"
where
//  subject.contractAddress == certificate_contract &&
//  subject.functionName == "hasCertificate" &&
  subject.<ethereum.contract>[0].value;
```

If you have policies for multiple contracts there are two options (both shown here in the commented sections):
1. You make a new policy set for each contract and mark the policy set with
`for subject.contractAddress == "addressOfTheContract"`
2. If you prefer to keep the policies in the same set you can make a global variable for each contract:
`var contract1 = "addressOfTheContract";` and then you can define the contract the policy belongs to in the where-section:
`subject.contractAddress == contract1`

This scheme is also helpful when calling different functions from a contract.
In this case you would check `subject.functionName == "nameOfTheFunction"` in the where-section.
