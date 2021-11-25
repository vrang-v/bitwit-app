package com.app.bitwit.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VotingOption {
    
    INCREMENT("increment"),
    DECREMENT("decrement");
    
    private final String option;
}
