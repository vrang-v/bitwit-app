package com.app.bitwit.util;

import android.util.AndroidRuntimeException;
import com.app.bitwit.common.BitwitHttpException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import retrofit2.Response;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {
    
    public static <T> T get2xxBody(Response<T> response) throws IOException {
        if (! response.isSuccessful( )) {
            throw new BitwitHttpException(response);
        }
        if (response.body( ) == null) {
            throw new AndroidRuntimeException( );
        }
        return response.body( );
    }
    
    public static <T> Empty get2xxEmptyBody(Response<T> response) throws IOException {
        if (! response.isSuccessful( )) {
            throw new BitwitHttpException(response);
        }
        return Empty.getInstance( );
    }
    
    public static <T> Response<T> filterHttpError(Response<T> response, int errorCode) throws IOException {
        if (! response.isSuccessful( ) && response.code( ) == errorCode) {
            throw new BitwitHttpException(response);
        }
        return response;
    }
    
    public static <T> Response<T> filter429Error(Response<T> response) throws IOException {
        if (! response.isSuccessful( ) && response.code( ) == 429) {
            throw new BitwitHttpException(response);
        }
        return response;
    }
}
