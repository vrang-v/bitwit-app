package com.app.bitwit.view.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.databinding.SignUpActivityBinding;
import com.app.bitwit.view.activity.FrameActivity;
import com.app.bitwit.viewmodel.SignUpViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.var;

import static com.app.bitwit.util.Callback.callback;
import static com.app.bitwit.util.LiveDataUtils.observe;
import static com.app.bitwit.util.StringUtils.isEmailFormat;
import static com.app.bitwit.view.activity.auth.SignUpProcessActivity.RESULT_FAILURE;
import static com.app.bitwit.view.activity.auth.SignUpProcessActivity.RESULT_SUCCESS;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {
    
    private static final int RC_GOOGLE_SIGN_IN  = 1;
    private static final int RC_SIGN_IN_PROCESS = 2;
    
    private static final String CLIENT_ID = "355754127095-5f1mrhut2fo7sitlvo90v07vhdhp992o.apps.googleusercontent.com";
    
    private SignUpActivityBinding binding;
    private SignUpViewModel       viewModel;
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
    
        viewModel.getSnackbar( ).observe(this, s ->
                Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        
        observe(this, viewModel.getEmail( ), email -> {
            if (isEmailFormat(email)) {
                binding.signUpText1.setText("");
                binding.signUpText2.setText("");
            }
            else {
                binding.signUpText1.setText("올바른 이메일이 맞나요?");
                binding.signUpText2.setText("다시 한번 확인해 주세요");
            }
        });
        
        binding.signUpButton.setOnClickListener(v -> {
            Consumer<Boolean> onSuccess = isDuplicate -> {
                if (isDuplicate == null) {
                    viewModel.setSnackbar("인증 도중 문제가 발생했습니다. 다시 시도해주세요.");
                    return;
                }
                if (isDuplicate) {
                    binding.warningText.setText("이미 존재하는 이메일입니다.");
                }
                else {
                    binding.warningText.setText("");
                    Intent intent = new Intent(getApplicationContext( ), SignUpProcessActivity.class)
                            .putExtra(ExtraKey.EMAIL, viewModel.getEmail( ).getValue( ))
                            .putExtra(ExtraKey.PASSWORD, viewModel.getPassword( ).getValue( ));
                    startActivityForResult(intent, RC_SIGN_IN_PROCESS);
                }
            };
            Consumer<Throwable> onError = e -> {
                Log.e("SignUpActivity", "checkForDuplicate", e);
                viewModel.setSnackbar("인증 도중 문제가 발생했습니다. 다시 시도해주세요.");
            };
            
            viewModel.checkForDuplicateEmail(callback(onSuccess, onError));
        });
        
        binding.loginText.setOnClickListener(v -> {
            var intent = new Intent(getApplicationContext( ), LoginActivity.class)
                    .putExtra(ExtraKey.EMAIL, "");
            startActivity(intent);
            finish( );
        });
        
        binding.googleLogin.setOnClickListener(v ->
                googleSignIn( )
        );
    }
    
    private void googleSignIn( ) {
        var signInOptions = new Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail( )
                .build( );
        var googleClient = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(googleClient.getSignInIntent( ), RC_GOOGLE_SIGN_IN);
    }
    
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            var googleAccount = completedTask.getResult(ApiException.class);
            if (googleAccount != null) {
                var request = new GoogleLoginRequest(googleAccount.getIdToken( ));
                viewModel.googleLogin(request, callback(
                        response -> {
                            var intent = new Intent(getApplicationContext( ), FrameActivity.class);
                            startActivity(intent);
                            finish( );
                        },
                        e -> {
                            Log.e("ERROR", "login", e);
                            binding.warningText.setText("구글 회원가입 실패");
                        }
                ));
            }
        }
        catch (ApiException e) {
            e.printStackTrace( );
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
        else if (requestCode == RC_SIGN_IN_PROCESS) {
            if (resultCode == RESULT_SUCCESS) {
                var intent = new Intent(getApplicationContext( ), LoginActivity.class)
                        .putExtra(ExtraKey.EMAIL, viewModel.getEmail( ).getValue( ))
                        .putExtra(ExtraKey.PASSWORD, viewModel.getPassword( ).getValue( ))
                        .putExtra(ExtraKey.SIGN_IN_SUCCESS, true);
                startActivity(intent);
                finish( );
            }
            else if (resultCode == RESULT_FAILURE) {
                viewModel.setSnackbar("회원 가입 도중 오류가 발생했습니다");
            }
        }
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.sign_up_activity);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
    
    @Override
    protected void onPause( ) {
        super.onPause( );
        overridePendingTransition(R.anim.slide_right_to_left_enter, R.anim.slide_right_to_left_exit);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime > 2000) {
                viewModel.setSnackbar("뒤로가기 버튼을 누르면 앱이 종료됩니다.");
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