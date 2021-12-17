package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.util.StringUtils;
import com.app.bitwit.util.subscription.Subscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;

@Getter
@HiltViewModel
public class LoginViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String> email       = new MutableLiveData<>( );
    private final MutableLiveData<String> password    = new MutableLiveData<>( );
    
    private final MutableLiveData<String> infoMessage = new MutableLiveData<>( );
    private final MutableLiveData<Boolean> isWarning   = new MutableLiveData<>( );
    
    @Inject
    public LoginViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public Subscription<LoginResponse> login( ) {
        var request = new LoginRequest(email.getValue( ), password.getValue( ));
        if (! isValidLoginRequest(request)) {
            return empty( );
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
    
    public void setInfoMessage(String message) {
        this.infoMessage.postValue(message);
        isWarning.postValue(false);
    }
    
    public void setWarningMessage(String message) {
        this.infoMessage.postValue(message);
        isWarning.postValue(true);
    }
    
    public void initMessage( ) {
        infoMessage.postValue(null);
        isWarning.postValue(false);
    }
}
