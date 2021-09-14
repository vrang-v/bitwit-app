package com.app.bitwit.util;

import com.app.bitwit.data.source.storage.LocalStorage;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;

import static com.app.bitwit.util.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuthenticationInterceptor implements Interceptor {
    
    private final LocalStorage localStorage;
    
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Builder builder = chain.request( ).newBuilder( );
        localStorage.load("jwt")
                    .ifPresent(jwt -> builder.header(AUTHORIZATION, jwt.startsWith("Bearer ") ? jwt : "Bearer " + jwt));
        return chain.proceed(builder.build( ));
    }
}

