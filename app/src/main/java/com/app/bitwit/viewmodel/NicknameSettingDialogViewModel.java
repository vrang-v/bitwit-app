package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.UpdateAccountRequest;
import com.app.bitwit.domain.Account;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;

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
    
    public void checkForDuplicateNickname(String nickname) {
        addDisposable(
                accountRepository
                        .isDuplicateNickname(nickname)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(isDuplicate -> {
                            valid.postValue(! isDuplicate);
                            infoMessage.postValue(isDuplicate.equals(TRUE) ? "중복된 닉네임입니다" : "사용 가능한 닉네임입니다");
                        })
        );
    }
    
    public void changeNickname(Consumer<Account> onSuccess, Consumer<Throwable> onError, Runnable onNotValid) {
        if (! valid.getValue( ).equals(TRUE)) {
            onNotValid.run( );
            return;
        }
        var request = new UpdateAccountRequest( );
        request.setNickname(nickname.getValue( ));
        addDisposable(
                accountRepository
                        .updateAccount(request)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onSuccess, onError)
        );
    }
    
    public boolean isValidFormat(String nickname) {
        boolean valid = 2 <= nickname.trim( ).length( ) && nickname.trim( ).length( ) < 10;
        if (! valid) {
            this.valid.postValue(FALSE);
            infoMessage.postValue("닉네임은 2~9글자로 설정해주세요");
        }
        return valid;
    }
}
