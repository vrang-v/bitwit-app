package com.app.bitwit.view.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.app.bitwit.R;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.source.remote.dto.request.CreateAccountRequest;
import com.app.bitwit.databinding.ActivitySignUpProcessBinding;
import com.app.bitwit.view.activity.WebViewActivity;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.var;

import javax.inject.Inject;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class SignUpProcessActivity extends AppCompatActivity {
    
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILURE = 1;
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
    @Inject AccountRepository accountRepository;
    
    private ActivitySignUpProcessBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_process);
        
        Bundle extras = getIntent( ).getExtras( );
        
        String email    = extras.getString("email");
        String password = extras.getString("password");
        
        binding.email.setText(email);
        
        binding.signUpButton.setOnClickListener(v -> {
            String passwordCheck = binding.passwordEdit.getText( ).toString( );
            if (! password.equals(passwordCheck)) {
                Snackbar.make(binding.getRoot( ), "비밀번호가 일치하지 않습니다", LENGTH_SHORT).show( );
                return;
            }
            signUp(email, password);
        });
    }
    
    private void signUp(String email, String password) {
        compositeDisposable.add(
                accountRepository
                        .createAccount(new CreateAccountRequest("test", email, password))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(
                                unused -> {
                                    String emailSiteURL = getEmailSiteLink(email);
                                    if (emailSiteURL != null) {
                                        var intent = new Intent(getApplicationContext( ), WebViewActivity.class)
                                                .putExtra(WebViewActivity.URL, emailSiteURL);
                                        startActivity(intent);
                                        setResult(RESULT_SUCCESS);
                                        finish( );
                                    }
                                },
                                e -> {
                                    setResult(RESULT_FAILURE);
                                    finish( );
                                }
                        )
        );
    }
    
    private String getEmailSiteLink(String email) {
        var domain = email.split("@")[1];
        if (domain.equals("naver.com")) {
            return "https://mail.naver.com";
        }
        else if (domain.equals("gmail.com")) {
            return "https://mail.google.com";
        }
        return null;
    }
    
    @Override
    protected void onDestroy( ) {
        compositeDisposable.dispose( );
        super.onDestroy( );
    }
}