package com.app.bitwit.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.domain.Account;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;

import static android.util.Patterns.EMAIL_ADDRESS;

@Getter
@HiltViewModel
public class SignUpViewModel extends DisposableViewModel {
    
    private final AccountRepository accountRepository;
    private final SavedStateHandle  savedStateHandle;
    
    private final MutableLiveData<String> email    = new MutableLiveData<>( );
    private final MutableLiveData<String> password = new MutableLiveData<>( );
    private final MutableLiveData<String> text1    = new MutableLiveData<>( );
    private final MutableLiveData<String> text2    = new MutableLiveData<>( );
    private final MutableLiveData<String> warning  = new MutableLiveData<>( );
    private final MutableLiveData<String> snackbar = new MutableLiveData<>( );
    
    private final MutableLiveEvent<Void> completeBtnClick      = new MutableLiveEvent<>( );
    private final MutableLiveEvent<Void> navigateLoginBtnClick = new MutableLiveEvent<>( );
    
    @Inject
    public SignUpViewModel(AccountRepository accountRepository, SavedStateHandle savedStateHandle) {
        this.accountRepository = accountRepository;
        this.savedStateHandle  = savedStateHandle;
    }
    
    public void signUp(Consumer<Account> onComplete, Consumer<Throwable> onError) {
        var request = new CreateAccountRequest("test", email.getValue( ), password.getValue( ));
        
        if (! isValidSignUpRequest(request)) { return; }
        
        addDisposable(
                accountRepository
                        .createAccount(request)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onComplete, onError)
        );
    }
    
    public void checkForDuplicateEmail(Consumer<Boolean> onSuccess, Consumer<Throwable> onError) {
        var request = new CreateAccountRequest("test", email.getValue( ), password.getValue( ));
    
        if (! isValidSignUpRequest(request)) { return; }
    
        addDisposable(
                accountRepository
                        .isDuplicateEmail(request.getEmail( ))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onSuccess, onError)
        );
    }
    
    public void googleLogin(GoogleLoginRequest request, Consumer<LoginResponse> onComplete, Consumer<Throwable> onError) {
        addDisposable(
                accountRepository
                        .googleLogin(request)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onComplete, onError)
        );
    }
    
    private boolean isValidSignUpRequest(CreateAccountRequest request) {
        if (TextUtils.isEmpty(request.getEmail( ))) {
            warning.postValue("이메일을 입력해주세요");
            return false;
        }
        if (TextUtils.isEmpty(request.getPassword( ))) {
            warning.postValue("비밀번호를 입력해주세요");
            return false;
        }
        if (! EMAIL_ADDRESS.matcher(request.getEmail( )).matches( )) {
            warning.postValue("이메일 형식을 확인해주세요");
            return false;
        }
        if (request.getPassword( ).length( ) < 6) {
            warning.postValue("비밀번호를 6자리 이상으로 설정해주세요");
            return false;
        }
        return true;
    }
    
    public void makeSnackbar(String message) {
        snackbar.postValue(message);
    }
    
    public void publishCompleteBtnClick( ) {
        completeBtnClick.publish( );
    }
    
    public void publishNavigateLoginBtnClick( ) {
        navigateLoginBtnClick.publish( );
    }
}
