# Sapl-Ethereum Documentation

## loadContractInformation

This function was added to provide a simple, user-friendly way of retreiving information from a contract on the Ethereum Blockchain. It needs to receive a JsonNode with the following information:

 - "fromAccount":  The account which the request is send from
 - "toAccount":    The address of the contract that should be called
 - "functionName": The name of the function that should be called
 - "inputParams":  The type and value of all input params that the called function requires
 - "outputParams": The type of all output params that the function returns.

All types that can be used are listed in the convertToType-method of the [EthereumPipFunctions](https://github.com/heutelbeck/sapl-policy-engine/blob/sapl-ethereum/sapl-ethereum/src/main/java/io/sapl/interpreter/pip/EthereumPipFunctions.java).

 
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
	"fromAccount":"0x70b6613e37616045a80a97e08e930e1e4d800039",
	"toAccount":"0x2d53b58c67ba813c2d1962f8a712ef5533c07c59",
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