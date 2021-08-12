package com.app.bitwit.domain;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class VoteResponse {
    
    Long id;
    
    Stock stock;
    
    String description;
    
    String startAt;
    
    String endedAt;
    
    String createdAt;
    
    String updatedAt;
    
    int participantCount;
    
    Map<VotingOption, Integer> selectionCount;
    
    List<Ballot> ballots;
}
