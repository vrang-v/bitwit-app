package com.app.bitwit.data.source.remote.dto.request;

import lombok.Value;

@Value
public class LoginRequest {
    String email;
    String password;
}
