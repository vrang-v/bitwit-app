package com.app.bitwit.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.view.activity.auth.LoginActivity;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.var;

import javax.inject.Inject;

@AndroidEntryPoint
public class EntryPointActivity extends AppCompatActivity {
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
    @Inject AccountRepository accountRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Consumer<LoginResponse> onSuccess = account -> {
            Intent intent;
            if (account.isEmailVerified( )) {
                intent = new Intent(getApplicationContext( ), FrameActivity.class);
            }
            else {
                intent = new Intent(getApplicationContext( ), LoginActivity.class);
            }
            startActivity(intent);
            finish( );
        };
        
        Consumer<Throwable> onError = throwable -> {
            var intent = new Intent(getApplicationContext( ), LoginActivity.class);
            startActivity(intent);
            finish( );
        };
        
        compositeDisposable.add(
                accountRepository
                        .loadAccount( )
                        .flatMap(accountRepository::jwtLogin)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onSuccess, onError)
        );
    }
    
    @Override
    protected void onPause( ) {
        super.onPause( );
        overridePendingTransition(0, 0);
    }
    
    @Override
    protected void onDestroy( ) {
        compositeDisposable.dispose( );
        super.onDestroy( );
    }
}
