package com.app.bitwit.common;

import lombok.Getter;
import retrofit2.Response;

import java.io.IOException;

@Getter
public class BitwitHttpException extends RuntimeException {
    
    private final Integer status;
    private final String  errorMessage;
    
    public BitwitHttpException(Response<?> response) throws IOException {
        super(response.errorBody( ).string( ));
        this.status       = response.code( );
        this.errorMessage = response.errorBody( ).string( );
    }
}
