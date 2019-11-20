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

 
Let's assume that you want to call the function `isAuthorized` from the following contract:

```solidity
contract Authorization {

  struct User {
    bool authorized;
  }

  address public admin;

  mapping(address => User) public users;

  constructor() public {
    admin = msg.sender;
  }

  function authorize (address user) public {
    require(
      msg.sender == admin,
      "Only the admin can authorize users."
    );

    users[user].authorized = true;
  }

  function unauthorize (address user) public {
    require(
      msg.sender == admin,
      "Only the admin can unauthorize users."
    );

    users[user].authorized = false;
  }

  function isAuthorized(address user) public view
          returns (bool authorized_) {
    authorized_ = users[user].authorized;
  }
}
```

The contract has been published to the address `0x2d53b58c67ba813c2d1962f8a712ef5533c07c59`.
Furthermore, you want to know if the Ethereum user with the address `3f2cbea2185089ea5bbabbcd7616b215b724885c` is authorized.
In this case your JsonNode should look like that:


```json
{
	"contractAddress":"0x2d53b58c67ba813c2d1962f8a712ef5533c07c59",
	"functionName":"isAuthorized",
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
//var authorization = "0x2d53b58c67ba813c2d1962f8a712ef5533c07c59";


policy "test_eth_policy"
permit
  action=="access" & resource=="ethereum"
where
//  subject.contractAddress == authorization &&
//  subject.functionName == "isAuthorized" &&
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
