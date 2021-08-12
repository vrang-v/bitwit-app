package com.app.bitwit.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.LoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;

@Getter
@HiltViewModel
public class LoginViewModel extends DisposableViewModel {
    
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String> email    = new MutableLiveData<>( );
    private final MutableLiveData<String> password = new MutableLiveData<>( );
    private final MutableLiveData<String> toast    = new MutableLiveData<>( );
    
    private final MutableLiveEvent<Void> loginBtnClick  = new MutableLiveEvent<>( );
    private final MutableLiveEvent<Void> navigateSignUp = new MutableLiveEvent<>( );
    
    @Inject
    public LoginViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public void login(Consumer<LoginResponse> onComplete, Consumer<Throwable> onError) {
        var request = new LoginRequest(email.getValue( ), password.getValue( ));
        
        if (! isValidLoginRequest(request)) { return; }
        
        addDisposable(
                accountRepository
                        .login(request)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onComplete, onError)
        );
    }
    
    private boolean isValidLoginRequest(LoginRequest request) {
        if (TextUtils.isEmpty(request.getEmail( ))) {
            toast.postValue("이메일을 입력해주세요");
            return false;
        }
        if (TextUtils.isEmpty(request.getPassword( ))) {
            toast.postValue("비밀번호를 입력해주세요");
            return false;
        }
        return true;
    }
    
    public void loginBtnClick( ) {
        loginBtnClick.publish( );
    }
    
    public void navigateSignUp( ) {
        navigateSignUp.publish( );
    }
    
    public void setToast(String message) {
        toast.postValue(message);
    }
}
