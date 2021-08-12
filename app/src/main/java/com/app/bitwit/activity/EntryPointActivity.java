package com.app.bitwit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.app.bitwit.R;
import com.app.bitwit.activity.auth.LoginActivity;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.response.SimpleIdResponse;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.var;

import javax.inject.Inject;

@AndroidEntryPoint
public class EntryPointActivity extends AppCompatActivity {
    
    @Inject AccountRepository accountRepository;
    
    CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_point);
        
        Consumer<SimpleIdResponse> onSuccess = account -> {
            var intent = new Intent(getApplicationContext( ), MainActivity.class);
            startActivity(intent);
            finish( );
        };
        
        Consumer<Throwable> onError = throwable -> {
            Log.e("ERROR", "entryPoint", throwable);
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
    protected void onDestroy( ) {
        compositeDisposable.dispose( );
        super.onDestroy( );
    }
}
