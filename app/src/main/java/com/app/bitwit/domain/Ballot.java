package com.app.bitwit.domain;

import lombok.Data;

@Data
public class Ballot
{
    Long id;
    
    Account account;
    
    VoteResponse voteResponse;
    
    VotingOption votingOption;
    
    String createdAt;
    
    String updatedAt;
}
