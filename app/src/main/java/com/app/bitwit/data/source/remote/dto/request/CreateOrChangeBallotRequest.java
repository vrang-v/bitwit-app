package com.app.bitwit.data.source.remote.dto.request;

import com.app.bitwit.domain.VotingOption;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
public class CreateOrChangeBallotRequest
{
    @NotNull
    Long voteId;
    
    @NotNull
    VotingOption votingOption;
}