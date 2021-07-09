package com.app.bitwit.domain;

import lombok.Data;

@Data
public class Ballot
{
    Long id;
    
    Account account;
    
    Vote vote;
    
    VotingOption votingOption;
    
    String createdAt;
    
    String updatedAt;
}
