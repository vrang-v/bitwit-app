package com.app.bitwit.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Ballot {
    
    @SerializedName("ballotId")
    Long id;
    
    Long voteId;
    
    Account account;
    
    Vote vote;
    
    VotingOption votingOption;
    
    String createdAt;
    
    String updatedAt;
    
    String status;
}
