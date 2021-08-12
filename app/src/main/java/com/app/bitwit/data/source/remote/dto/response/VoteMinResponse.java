package com.app.bitwit.data.source.remote.dto.response;

import com.app.bitwit.domain.Stock;
import com.app.bitwit.domain.VotingOption;
import lombok.Data;

import java.util.Map;

@Data
public class VoteMinResponse
{
    Long id;
    
    Stock stock;
    
    String description;
    
    String startAt;
    
    String endedAt;
    
    String createdAt;
    
    String updatedAt;
    
    int participantsCount;
    
    Map<VotingOption, Integer> selectionsCount;
}
