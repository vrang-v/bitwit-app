package com.app.bitwit.view.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.databinding.LoginActivityBinding;
import com.app.bitwit.view.activity.FrameActivity;
import com.app.bitwit.viewmodel.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.Callback.callback;
import static com.app.bitwit.util.LiveDataUtils.observeHasText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    
    private static final int RC_SIGN_IN = 1;
    
    private static final String CLIENT_ID = "355754127095-5f1mrhut2fo7sitlvo90v07vhdhp992o.apps.googleusercontent.com";
    
    private LoginActivityBinding binding;
    private LoginViewModel       viewModel;
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
    
        viewModel.getSnackbar( ).observe(this, s ->
                Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        
        binding.confirmBtn.setOnClickListener(v ->
                viewModel.login(callback(response -> handleLoginSuccess( ), e -> handleLoginError( )))
        );
        
        binding.signUpText.setOnClickListener(v -> {
            var intent = new Intent(getApplicationContext( ), SignUpActivity.class)
                    .putExtra(ExtraKey.EMAIL, "");
            startActivity(intent);
            finish( );
            overridePendingTransition(R.anim.anim_slide_left_to_right_enter, R.anim.anim_slide_left_to_right_exit);
        });
        
        binding.forgotPasswordText.setOnClickListener(v -> {
            var intent = new Intent(getApplicationContext( ), PasswordRetrievalActivity.class)
                    .putExtra(ExtraKey.EMAIL, viewModel.getEmail( ).getValue( ));
            startActivity(intent);
            finish( );
            overridePendingTransition(R.anim.anim_slide_right_to_left_enter, R.anim.anim_slide_right_to_left_exit);
        });
        
        binding.googleLogin.setOnClickListener(
                v -> googleSignIn( )
        );
    }
    
    private void handleLoginSuccess( ) {
        viewModel.setSnackbar("반갑습니다");
        var intent = new Intent(getApplicationContext( ), FrameActivity.class);
        startActivity(intent);
        finish( );
    }
    
    private void handleLoginError( ) {
        viewModel.setSnackbar("로그인 실패");
        binding.warning1Text.setText("아이디 또는 비밀번호가 일치하지 않거나");
        binding.warning2Text.setText("네트워크에 연결되어 있지 않습니다");
        binding.warning2Text.setVisibility(View.VISIBLE);
        binding.loginText.setVisibility(View.INVISIBLE);
    }
    
    private void googleSignIn( ) {
        var signInOptions = new Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail( )
                .build( );
        GoogleSignInClient googleClient = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(googleClient.getSignInIntent( ), RC_SIGN_IN);
    }
    
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            var googleAccount = completedTask.getResult(ApiException.class);
            if (googleAccount != null) {
                var request = new GoogleLoginRequest(googleAccount.getIdToken( ));
                viewModel.googleLogin(request, callback(response -> handleLoginSuccess( ), e -> handleLoginError( )));
            }
        }
        catch (ApiException e) {
            e.printStackTrace( );
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.login_activity);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    
        Bundle extras = getIntent( ).getExtras( );
        if (extras != null) {
            binding.emailEdit.setText(extras.getString(ExtraKey.EMAIL));
            binding.passwordEdit.setText(extras.getString(ExtraKey.PASSWORD));
            if (extras.getBoolean(ExtraKey.SIGN_IN_SUCCESS)) {
                binding.loginText.setText("로그인 버튼을 눌러주세요");
            }
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime > 2000) {
                viewModel.setSnackbar("뒤로가기 버튼을 누르면 앱이 종료됩니다");
                backButtonKeyDownTime = System.currentTimeMillis( );
            }
            else {
                finish( );
            }
        }
        else {
            result = super.onKeyDown(keyCode, event);
        }
        return result;
    }
}
