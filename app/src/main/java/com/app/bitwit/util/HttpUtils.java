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
}
