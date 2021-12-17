package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.domain.Account;
import com.app.bitwit.util.IOUtils;
import com.app.bitwit.util.subscription.CompletableSubscription;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.util.subscription.Subscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;
import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;

import javax.inject.Inject;
import java.io.InputStream;

@Getter
@HiltViewModel
public class AccountViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<Account> account = new MutableLiveData<>( );
    
    @Inject
    public AccountViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public Subscription<Account> loadAccount( ) {
        return subscribe(
                accountRepository
                        .getAccount( )
                        .doOnSuccess(account::postValue)
        );
    }
    
    public SingleSubscription<Account> changeProfileImage(String fileName, InputStream imageInputStream) {
        var imageBytes  = IOUtils.readAllBytes(imageInputStream);
        var requestBody = RequestBody.create(imageBytes, MediaType.parse("image/*"));
        var imageFile   = Part.createFormData("file", fileName, requestBody);
        return subscribe(
                accountRepository.changeProfileImage(imageFile)
                                 .doOnSuccess(account::postValue)
        );
    }
    
    public CompletableSubscription logout( ) {
        return subscribe(
                accountRepository.clearLoginToken( )
        );
    }
}
