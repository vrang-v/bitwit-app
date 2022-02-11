package com.app.bitwit.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class Vote {
    
    @SerializedName("voteId")
    Long id;
    
    Stock stock;
    
    String description;
    
    LocalDateTime startAt;
    
    LocalDateTime endedAt;
    
    String createdAt;
    
    String updatedAt;
    
    int participantsCount;
    
    Map<VotingOption, Integer> selectionCount;
    
    List<Ballot> ballots;
    
}
