package com.app.bitwit.data.source.remote;

import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.response.DuplicateCheckResponse;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.data.source.remote.dto.response.SimpleIdResponse;
import com.app.bitwit.domain.Account;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccountServiceClient {
    
    @POST("/api/accounts")
    Single<Response<Account>> createAccount(@Body CreateAccountRequest request);
    
    @GET("/api/accounts/me")
    Single<Response<Account>> getAccount( );
    
    @POST("/api/login")
    Single<Response<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("/api/login/jwt")
    Single<Response<SimpleIdResponse>> jwtLogin( );
    
    @POST("/api/login/google")
    Single<Response<LoginResponse>> googleLogin(@Body GoogleLoginRequest request);
    
    @GET("/api/accounts/duplicate-check/email")
    Single<Response<DuplicateCheckResponse>> checkForDuplicateEmail(@Query("email") String email);
}
