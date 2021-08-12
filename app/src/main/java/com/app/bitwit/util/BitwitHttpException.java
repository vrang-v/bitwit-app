package com.app.bitwit.util;

import lombok.Getter;
import retrofit2.Response;

import java.io.IOException;

@Getter
public class BitwitHttpException extends RuntimeException
{
    private final Integer status;
    private final String  errorMessage;
    
    public BitwitHttpException(Response<?> response) throws IOException
    {
        super( );
        this.status       = response.code( );
        this.errorMessage = response.errorBody( ).string( );
    }
}
