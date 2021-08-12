package com.app.bitwit.data.repository;

import com.app.bitwit.data.source.remote.AccountServiceClient;
import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.data.source.remote.dto.response.SimpleIdResponse;
import com.app.bitwit.data.source.storage.LocalStorage;
import com.app.bitwit.data.source.storage.LocalStorageKey;
import com.app.bitwit.util.HttpUtils;
import com.app.bitwit.util.LoginAccount;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.var;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AccountRepository
{
    private final AccountServiceClient accountServiceClient;
    private final LocalStorage         localStorage;
    
    public Single<SimpleIdResponse> createAccount(CreateAccountRequest request)
    {
        return accountServiceClient
                .createAccount(request)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<LoginResponse> login(LoginRequest request)
    {
        return accountServiceClient
                .login(request)
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(response -> {
                    var loginAccount = new LoginAccount(response.getId( ), response.getEmail( ), response.getName( ));
                    localStorage.save(LocalStorageKey.ACCOUNT, loginAccount)
                                .save(LocalStorageKey.JWT, response.getJwt( ));
                });
    }
    
    public Single<SimpleIdResponse> jwtLogin(LoginAccount loginAccount)
    {
        Long accountId = loginAccount.getId( );
        return accountServiceClient
                .jwtLogin( )
                .map(HttpUtils::get2xxBody)
                .doOnSuccess(response -> {
                    if (! response.getId( ).equals(accountId)) {
                        throw new IllegalStateException( );
                    }
                });
    }
    
    public Single<LoginAccount> loadAccount( )
    {
        return localStorage
                .load(LocalStorageKey.ACCOUNT, LoginAccount.class)
                .filter(account -> account.getId( ) != null)
                .map(Single::just)
                .orElseGet(( ) -> Single.error(IllegalArgumentException::new));
    }
}
