# Sapl-Ethereum Documentation

## The usage of loadContractInformation

This Pip-Function needs to receive a JsonNode of the following form: 

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