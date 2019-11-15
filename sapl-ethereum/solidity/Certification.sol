pragma solidity >=0.4.16 <0.7.0;

contract Certification {

  struct Certificate {
    address issuer;
    string name;
  }

  struct Graduate {
    Certificate[] certificates;
  }


  mapping(address => Graduate) graduates;

  constructor() public {
  }

  function issueCertificate (address graduate, string memory certificateName) public {
    graduates[graduate].certificates.push(Certificate(msg.sender, certificateName));
  }


  function hasCertificate(address graduate, address certificateIssuer, string memory certificateName) public view
          returns (bool certificateOwned_) {
    Certificate[] memory certArray = graduates[graduate].certificates;
    for (uint i = 0; i < certArray.length; i++ ){
      if (certArray[i].issuer == certificateIssuer && equalStrings(certArray[i].name, certificateName) ) {
        return true;
      }
    }
    return false;
  }
  
  function certIssuerAddress(address graduate) public view returns (address issuer_ {
      return graduates[graduate].certificates[0].issuer;
  }


  function equalStrings (string memory a, string memory b) private pure
       returns (bool) {
         return (keccak256(abi.encodePacked((a))) == keccak256(abi.encodePacked((b))) );

       }



}
