package com.app.bitwit.common;

import lombok.Getter;

import java.time.Instant;

@Getter
public class BaseTimeEntity
{
    private Instant createdAt;
    
    private Instant updatedAt;
}
