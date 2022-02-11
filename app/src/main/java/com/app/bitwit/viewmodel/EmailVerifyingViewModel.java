package com.app.bitwit.viewmodel;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.common.BitwitHttpException;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.response.EmailVerificationResponse;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.util.Empty;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

import javax.inject.Inject;

@Getter
@HiltViewModel
public class EmailVerifyingViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String>  infoMessage     = new MutableLiveData<>( );
    private final MutableLiveData<Boolean> isWarning       = new MutableLiveData<>( );
    private final MutableLiveData<Boolean> resendBtnEnable = new MutableLiveData<>(true);
    
    private LoginAccount loginAccount;
    
    @Inject
    public EmailVerifyingViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        loadAccount( );
    }
    
    private void loadAccount( ) {
        accountRepository.loadAccount( )
                         .doOnSuccess(account -> this.loginAccount = account)
                         .subscribe( );
    }
    
    public SingleSubscription<Empty> resendEmailToken( ) {
        if (loginAccount == null) {
            loadAccount( );
        }
        return subscribe(
                accountRepository
                        .resendEmailToken(loginAccount.getId( ))
                        .doOnError(e -> {
                            if (e instanceof BitwitHttpException && ((BitwitHttpException)e).getStatus( ) == 429) {
                                setEmailResendTimeMessage( );
                            }
                            else {
                                setWarningMessage("서버에 오류가 발생했습니다");
                            }
                        })
        );
    }
    
    private void setEmailResendTimeMessage( ) {
        resendBtnEnable.postValue(false);
        for (int i = 59; i >= 0; i--) {
            int finalI = i;
            new Handler(Looper.getMainLooper( )).postDelayed(( ) -> {
                if (finalI == 0) {
                    initMessage( );
                    resendBtnEnable.postValue(true);
                }
                else {
                    setWarningMessage(finalI + "초 후에 이메일을 다시 보낼 수 있습니다");
                }
            }, 59000L - (1000L * i));
        }
    }
    
    public SingleSubscription<EmailVerificationResponse> isEmailVerifiedAccount( ) {
        if (loginAccount == null) {
            loadAccount( );
        }
        return subscribe(
                accountRepository
                        .isEmailVerifiedAccount(loginAccount.getId( ))
                        .doOnError(e -> setWarningMessage("서버에 오류가 발생했습니다"))
        );
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
