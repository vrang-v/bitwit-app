package com.app.bitwit.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.util.Callback;
import com.app.bitwit.util.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;

@Getter
@HiltViewModel
public class LoginViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String> email    = new MutableLiveData<>( );
    private final MutableLiveData<String> password = new MutableLiveData<>( );
    
    @Inject
    public LoginViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public void login(Callback<LoginResponse> callback) {
        var request = new LoginRequest(email.getValue( ), password.getValue( ));
        
        if (! isValidLoginRequest(request)) { return; }
        
        subscribe(callback, accountRepository.login(request));
    }
    
    public void googleLogin(GoogleLoginRequest request, Callback<LoginResponse> callback) {
        subscribe(callback, accountRepository.googleLogin(request));
    }
    
    private boolean isValidLoginRequest(LoginRequest request) {
        if (TextUtils.isEmpty(request.getEmail( ))) {
            setSnackbar("이메일을 입력해주세요");
            return false;
        }
        if (TextUtils.isEmpty(request.getPassword( ))) {
            setSnackbar("비밀번호를 입력해주세요");
            return false;
        }
        return true;
    }
}
