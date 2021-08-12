package com.app.bitwit.data.source.remote;

import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.response.AccountResponse;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.data.source.remote.dto.response.SimpleIdResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AccountServiceClient {
    
    @POST("/api/v1/accounts")
    Single<Response<SimpleIdResponse>> createAccount(@Body CreateAccountRequest request);
    
    @GET("/api/v1/accounts/me")
    Single<Response<AccountResponse>> getAccount( );
    
    @POST("/api/v1/login")
    Single<Response<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("/api/v1/login/jwt")
    Single<Response<SimpleIdResponse>> jwtLogin( );
}
