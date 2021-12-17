package com.app.bitwit.data.source.remote;

import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.request.UpdateAccountRequest;
import com.app.bitwit.data.source.remote.dto.response.DuplicateCheckResponse;
import com.app.bitwit.data.source.remote.dto.response.EmailVerificationResponse;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.domain.Account;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.*;

public interface AccountServiceClient {
    
    @POST("/api/accounts")
    Single<Response<Account>> createAccount(@Body CreateAccountRequest request);
    
    @Multipart
    @POST("/api/accounts/profile-image")
    Single<Response<Account>> changeProfileImage(@Part MultipartBody.Part imageFile);
    
    @GET("/api/accounts/me")
    Single<Response<Account>> getAccount( );
    
    @PATCH("/api/accounts")
    Single<Response<Account>> updateAccount(@Body UpdateAccountRequest request);
    
    @POST("/api/login")
    Single<Response<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("/api/login/jwt")
    Single<Response<LoginResponse>> jwtLogin( );
    
    @POST("/api/login/google")
    Single<Response<LoginResponse>> googleLogin(@Body GoogleLoginRequest request);
    
    @GET("/api/accounts/duplicate-check")
    Single<Response<DuplicateCheckResponse>> checkForDuplicateEmail(@Query("email") String email);
    
    @GET("/api/accounts/duplicate-check")
    Single<Response<DuplicateCheckResponse>> checkForDuplicateNickname(@Query("name") String nickname);
    
    @GET("/api/accounts/email-verified-check")
    Single<Response<EmailVerificationResponse>> checkEmailVerified(@Query("accountId") Long accountId);
    
    @GET("/api/accounts/resend-email-token")
    Single<Response<Void>> resendEmailToken(@Query("accountId") Long accountId);
}
