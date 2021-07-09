package com.app.bitwit.domain;

import com.app.bitwit.common.BaseTimeEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Vote
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
    
    List<Ballot> ballots;
}
