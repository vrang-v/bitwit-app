package com.app.bitwit.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
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
public class SignUpViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String> email    = new MutableLiveData<>( );
    private final MutableLiveData<String> password = new MutableLiveData<>( );
    
    private final MutableLiveData<String>  infoMessage = new MutableLiveData<>( );
    private final MutableLiveData<Boolean> isWarning   = new MutableLiveData<>( );
    
    @Inject
    public SignUpViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public Subscription<Boolean> checkForDuplicateEmail( ) {
        var request = new CreateAccountRequest("init", email.getValue( ), password.getValue( ));
        if (! isValidRequest(request)) {
            return empty( );
        }
        return subscribe(accountRepository.isDuplicateEmail(request.getEmail( )));
    }
    
    public Subscription<LoginResponse> googleLogin(GoogleLoginRequest request) {
        return subscribe(accountRepository.googleLogin(request));
    }
    
    private boolean isValidRequest(CreateAccountRequest request) {
        if (! StringUtils.hasText(request.getEmail( ))) {
            setWarningMessage("이메일을 입력해주세요");
            return false;
        }
        if (TextUtils.isEmpty(request.getPassword( ))) {
            setWarningMessage("비밀번호를 입력해주세요");
            return false;
        }
        if (! StringUtils.isEmailFormat(request.getEmail( ))) {
            setWarningMessage("이메일 형식을 확인해주세요");
            return false;
        }
        if (request.getPassword( ).length( ) < 6) {
            setWarningMessage("비밀번호를 6자리 이상으로 설정해주세요");
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
