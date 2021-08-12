package com.app.bitwit.data.source.remote.dto.request;

import com.app.bitwit.domain.VotingOption;
import lombok.Data;

@Data
public class UpdateBallotRequest
{
    Long voteId;
    
    VotingOption option;
}
