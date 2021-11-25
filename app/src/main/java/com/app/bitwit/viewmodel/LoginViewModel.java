package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import com.app.bitwit.util.StringUtils;
import com.app.bitwit.util.subscription.Subscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
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
    
    public Subscription<LoginResponse> login( ) {
        var request = new LoginRequest(email.getValue( ), password.getValue( ));
        if (! isValidLoginRequest(request)) {
            return unsubscribe( );
        }
        return subscribe(accountRepository.login(request));
    }
    
    public Subscription<LoginResponse> googleLogin(GoogleLoginRequest request) {
        return subscribe(accountRepository.googleLogin(request));
    }
    
    private boolean isValidLoginRequest(LoginRequest request) {
        if (! StringUtils.hasText(request.getEmail( ))) {
            setSnackbar("이메일을 입력해주세요");
            return false;
        }
        if (! StringUtils.hasText(request.getPassword( ))) {
            setSnackbar("비밀번호를 입력해주세요");
            return false;
        }
        return true;
    }
}
