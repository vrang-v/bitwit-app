package com.app.bitwit.data.repository;

import com.app.bitwit.data.source.remote.AccountServiceClient;
import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.request.UpdateAccountRequest;
import com.app.bitwit.data.source.remote.dto.response.DuplicateCheckResponse;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.data.source.storage.LocalStorage;
import com.app.bitwit.data.source.storage.LocalStorageKey;
import com.app.bitwit.domain.Account;
import com.app.bitwit.util.HttpUtils;
import com.app.bitwit.dto.LoginAccount;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.var;

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
    
    public Single<Account> updateAccount(UpdateAccountRequest request) {
        return accountServiceClient
                .updateAccount(request)
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(account -> {
                    var loginAccount = new LoginAccount(account.getId( ), account.getEmail( ), account.getName( ));
                    localStorage.save(LocalStorageKey.ACCOUNT, loginAccount);
                });
    }
    
    public Single<LoginResponse> login(LoginRequest request) {
        return accountServiceClient
                .login(request)
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(response -> {
                    var loginAccount = new LoginAccount(response.getId( ), response.getEmail( ), response.getName( ));
                    localStorage.save(LocalStorageKey.ACCOUNT, loginAccount)
                                .save(LocalStorageKey.JWT, response.getJwt( ));
                });
    }
    
    public Single<LoginResponse> googleLogin(GoogleLoginRequest request) {
        return accountServiceClient
                .googleLogin(request)
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(response -> {
                    var loginAccount = new LoginAccount(response.getId( ), response.getEmail( ), response.getName( ));
                    localStorage.save(LocalStorageKey.ACCOUNT, loginAccount)
                                .save(LocalStorageKey.JWT, response.getJwt( ));
                });
    }
    
    public Single<LoginResponse> jwtLogin(LoginAccount loginAccount) {
        Long accountId = loginAccount.getId( );
        return accountServiceClient
                .jwtLogin( )
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(response -> {
                    if (! response.getId( ).equals(accountId)) {
                        throw new IllegalStateException( );
                    }
                    localStorage.save(LocalStorageKey.ACCOUNT,
                                        new LoginAccount(response.getId( ), response.getEmail( ), response.getName( ))
                                )
                                .save(LocalStorageKey.JWT, response.getJwt( ));
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
}
