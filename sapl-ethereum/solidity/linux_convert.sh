#!/bin/bash
# A tool to convert Solidity Contracts to their Java from usable with Web3j.
# The Converter creates abi and bin files in ./abi-bin and saves the Java form
# directly in ../src/main/java/io/sapl/ethereum/contracts
# Needed input is the name of the contract (without .sol)
# solc compiler needs to be installed
# (https://solidity.readthedocs.io/en/v0.5.7/installing-solidity.html)


WEB3J='./web3j-4.5.0/bin/web3j'

solc $1.sol --bin --abi --optimize -o ./abi-bin --overwrite
$WEB3J solidity generate -b ./abi-bin/$1.bin -a ./abi-bin/$1.abi -o ../src/test/java -p io.sapl.interpreter.pip.contracts
