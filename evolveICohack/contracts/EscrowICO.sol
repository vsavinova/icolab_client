pragma solidity ^0.4.11;

import "./StandardToken.sol";
import "./Owned.sol";

/// @author Evolve
contract EscrowICO is StandardToken, Owned{

  uint bonus = 50 ether;
  address team;                       //TODO initialize!

  modifier onlyTokenHolder() {
    require(balances[msg.sender]>0);
    _;

  }




  struct Request {
  string description;           //short description
  uint countWei;                //cost
  uint countVotes;
  bool isFinished;             //if request confirmed by the commity
  uint  countVotersPremia;             //how many peaple voted
  uint  countVotesPremia;              //how much votes they give, it depends on count of tokens that they have
  bool recievedPremia;
  mapping (address => bool) voters;
  mapping (address => bool) votersForPremia;
  }

  mapping (uint => Request) public requests;




  ///methods
  function addVotingRequest(string _description, uint _id, uint _countWei) onlyOwner{
    requests[_id] = Request(_description, _countWei,0,false,0,0,false);
  }



  function submitRequestion(uint id, bool result) onlyTokenHolder {       //result =true/false
    require(!requests[id].voters[msg.sender]);                      //check if holder doesnt give confirmation for this stage

    requests[id].voters[msg.sender] = true;
    requests[id].countVotes += balances[msg.sender];

    if(requests[id].countVotes > totalSupply / 2) {
        requests[id].isFinished = true;
        team.transfer(requests[id].countWei);
      }
  }

  function payBonus(uint x, uint id) internal {

    if(!requests[id].recievedPremia){

      requests[id].recievedPremia = true;
      team.transfer((bonus * x) / 100);
    }
  }


  function submitBonus(uint x, uint id) onlyTokenHolder{

    if(!requests[id].voters[msg.sender]){
      requests[id].voters[msg.sender] = true;
      requests[id].countVotersPremia += 1;
      requests[id].countVotesPremia += balances[msg.sender];

      if(requests[id].countVotersPremia > totalInvestors / 2){
        payBonus((requests[id].countVotesPremia * 100) / totalSupply, id );
      }
    }

  }

  function doInvest() payable {

    balances[msg.sender] = msg.value;
    totalSupply += msg.value;
    totalInvestors += 1;
}
}
