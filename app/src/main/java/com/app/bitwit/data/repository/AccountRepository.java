package com.app.bitwit.data.repository;

import com.app.bitwit.data.source.remote.AccountServiceClient;
import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.request.UpdateAccountRequest;
import com.app.bitwit.data.source.remote.dto.response.DuplicateCheckResponse;
import com.app.bitwit.data.source.remote.dto.response.EmailVerificationResponse;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.data.source.storage.LocalStorage;
import com.app.bitwit.data.source.storage.LocalStorageKey;
import com.app.bitwit.domain.Account;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.util.Empty;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.var;
import okhttp3.MultipartBody;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AccountRepository {
    
    private final AccountServiceClient accountServiceClient;
    private final LocalStorage         localStorage;
    
    public Single<Account> createAccount(CreateAccountRequest request) {
        return accountServiceClient
                .createAccount(request)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Account> getAccount( ) {
        return accountServiceClient
                .getAccount( )
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Account> updateAccount(UpdateAccountRequest request) {
        return accountServiceClient
                .updateAccount(request)
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(account -> {
                    var loginAccount = new LoginAccount(
                            account.getId( ), account.getEmail( ), account.getName( ), account.getEmailVerified( )
                    );
                    localStorage.save(LocalStorageKey.ACCOUNT, loginAccount);
                });
    }
    
    public Single<Account> changeProfileImage(MultipartBody.Part image) {
        return accountServiceClient
                .changeProfileImage(image)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<LoginResponse> login(LoginRequest request) {
        return accountServiceClient
                .login(request)
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(response -> {
                    var loginAccount = new LoginAccount(response);
                    localStorage.save(LocalStorageKey.ACCOUNT, loginAccount)
                                .save(LocalStorageKey.JWT, response.getJwt( ));
                });
    }
    
    public Single<LoginResponse> googleLogin(GoogleLoginRequest request) {
        return accountServiceClient
                .googleLogin(request)
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(response -> {
                    var loginAccount = new LoginAccount(response);
                    localStorage.save(LocalStorageKey.ACCOUNT, loginAccount)
                                .save(LocalStorageKey.JWT, response.getJwt( ));
                });
    }
    
    public Single<LoginResponse> jwtLogin(LoginAccount loginAccount) {
        Long accountId = loginAccount.getId( );
        return accountServiceClient
                .jwtLogin( )
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(loginResponse -> {
                    if (! loginResponse.getId( ).equals(accountId)) {
                        throw new IllegalStateException( );
                    }
                    localStorage.save(LocalStorageKey.ACCOUNT, new LoginAccount(loginResponse))
                                .save(LocalStorageKey.JWT, loginResponse.getJwt( ));
                });
    }
    
    public Single<Boolean> isDuplicateEmail(String email) {
        return accountServiceClient
                .checkForDuplicateEmail(email)
                .map(HttpUtils::get2xxBody)
                .map(DuplicateCheckResponse::isDuplicate);
    }
    
    public Single<Boolean> isDuplicateNickname(String nickname) {
        return accountServiceClient
                .checkForDuplicateNickname(nickname)
                .map(HttpUtils::get2xxBody)
                .map(DuplicateCheckResponse::isDuplicate);
    }
    
    public Single<LoginAccount> loadAccount( ) {
        return localStorage
                .load(LocalStorageKey.ACCOUNT, LoginAccount.class)
                .filter(account -> account.getId( ) != null)
                .map(Single::just)
                .orElseGet(( ) -> Single.error(IllegalArgumentException::new));
    }
    
    public Completable clearLoginToken( ) {
        return Completable.fromAction(( ) ->
                localStorage.clear(LocalStorageKey.ACCOUNT)
                            .clear(LocalStorageKey.JWT)
        );
    }
    
    public Single<EmailVerificationResponse> isEmailVerifiedAccount(Long accountId) {
        return accountServiceClient
                .checkEmailVerified(accountId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Empty> resendEmailToken(Long accountId) {
        return accountServiceClient
                .resendEmailToken(accountId)
                .map(HttpUtils::get2xxEmptyBody);
    }
}
