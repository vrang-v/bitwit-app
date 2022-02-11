package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.UpdateAccountRequest;
import com.app.bitwit.domain.Account;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Getter
@HiltViewModel
public class NicknameSettingDialogViewModel extends RxJavaViewModelSupport {
    
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String>  nickname    = new MutableLiveData<>( );
    private final MutableLiveData<String>  infoMessage = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> valid       = new MutableLiveData<>(FALSE);
    
    @Inject
    public NicknameSettingDialogViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public SingleSubscription<Boolean> checkForDuplicateNickname(String nickname) {
        return subscribe(
                accountRepository
                        .isDuplicateNickname(nickname)
                        .doOnSuccess(isDuplicate -> valid.postValue(! isDuplicate))
        );
    }
    
    public SingleSubscription<Account> changeNickname( ) {
        if (! Objects.equals(valid.getValue( ), TRUE)) {
            return SingleSubscription.empty( );
        }
        var request = new UpdateAccountRequest( );
        request.setNickname(nickname.getValue( ));
        
        return subscribe(accountRepository.updateAccount(request));
    }
    
    public boolean isValidFormat(String nickname) {
        int     nicknameLength = nickname.trim( ).length( );
        boolean isValid        = 2 <= nicknameLength && nicknameLength < 10;
        if (! isValid) {
            this.valid.postValue(FALSE);
            this.infoMessage.postValue("닉네임은 2~9글자로 설정해주세요");
        }
        return isValid;
    }
    
    public void setInfoMessage(String message) {
        this.infoMessage.postValue(message);
    }
}
