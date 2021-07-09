package com.app.bitwit.webclient.dto.request;

import com.app.bitwit.domain.VotingOption;
import lombok.Data;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
public class BallotRequest
{
    @NotNull
    Long accountId;
    
    @NotNull
    Long voteId;
    
    @NotNull
    VotingOption votingOption;
}