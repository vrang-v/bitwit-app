package com.app.bitwit.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.activity.MainActivity;
import com.app.bitwit.data.source.remote.AccountServiceClient;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.databinding.SignUpActivityBinding;
import com.app.bitwit.viewmodel.SignUpViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.var;

import javax.inject.Inject;

import static android.util.Patterns.EMAIL_ADDRESS;
import static com.app.bitwit.activity.auth.SignUpProcessActivity.RESULT_FAILURE;
import static com.app.bitwit.activity.auth.SignUpProcessActivity.RESULT_SUCCESS;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {
    
    private static final int RC_GOOGLE_SIGN_IN  = 1;
    private static final int RC_SIGN_IN_PROCESS = 2;
    
    private static final String CLIENT_ID = "355754127095-5f1mrhut2fo7sitlvo90v07vhdhp992o.apps.googleusercontent.com";
    
    @Inject AccountServiceClient accountServiceClient;
    
    private SignUpActivityBinding binding;
    private SignUpViewModel       viewModel;
    private GoogleSignInClient    googleClient;
    private long                  backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        viewModel.getSnackbar( )
                 .observe(this, s -> Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( ));
        
        viewModel.getEmail( )
                 .observe(this, password -> {
                     String email = viewModel.getEmail( ).getValue( );
                     if (email != null && ! EMAIL_ADDRESS.matcher(email).matches( )) {
                         binding.signUpText1.setText("입력된 이메일에 문제가 있어요");
                         binding.signUpText2.setText("제대로 된 이메일인지 다시 한번 확인해 주세요");
                     }
                     else {
                         binding.signUpText1.setText("");
                         binding.signUpText2.setText("");
                     }
                 });
        
        viewModel.getCompleteBtnClick( )
                 .observe(this, unused -> {
                             Consumer<Boolean> onSuccess = isDuplicate -> {
                                 if (isDuplicate == null) {
                                     viewModel.makeSnackbar("인증 도중 문제가 발생했습니다. 다시 시도해주세요.");
                                     return;
                                 }
                                 if (isDuplicate) {
                                     binding.warningText.setText("이미 존재하는 이메일입니다.");
                                 }
                                 else {
                                     binding.warningText.setText("");
                                     Intent intent = new Intent(getApplicationContext( ), SignUpProcessActivity.class)
                                             .putExtra("email", viewModel.getEmail( ).getValue( ))
                                             .putExtra("password", viewModel.getPassword( ).getValue( ));
                                     startActivityForResult(intent, RC_SIGN_IN_PROCESS);
                                 }
                             };
                             Consumer<Throwable> onError = e -> {
                                 viewModel.makeSnackbar("인증 도중 문제가 발생했습니다. 다시 시도해주세요.");
                                 Log.e("ERROR", "checkForDuplicate", e);
                             };
                    
                             viewModel.checkForDuplicateEmail(onSuccess, onError);
                         }
                 );
        
        viewModel.getNavigateLoginBtnClick( )
                 .observe(this, unused -> {
                     var intent = new Intent(getApplicationContext( ), LoginActivity.class).putExtra("email", "");
                     startActivity(intent);
                     finish( );
                 });
        
        var signInOptions = new Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail( )
                .build( );
        googleClient = GoogleSignIn.getClient(this, signInOptions);
        
        binding.googleLogin.setOnClickListener(
                v -> startActivityForResult(googleClient.getSignInIntent( ), RC_GOOGLE_SIGN_IN)
        );
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
                Intent intent = new Intent(getApplicationContext( ), LoginActivity.class)
                        .putExtra("email", viewModel.getEmail( ).getValue( ))
                        .putExtra("password", viewModel.getPassword( ).getValue( ))
                        .putExtra("textViewLogin", true);
                startActivity(intent);
                finish( );
            }
            else if (resultCode == RESULT_FAILURE){
                Snackbar.make(binding.getRoot( ), "회원 가입 도중 오류가 발생했습니다", LENGTH_SHORT).show( );
            }
        }
    }
    
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            var googleAccount = completedTask.getResult(ApiException.class);
            if (googleAccount != null) {
                
                Consumer<LoginResponse> onComplete = response -> {
                    var intent = new Intent(getApplicationContext( ), MainActivity.class);
                    startActivity(intent);
                    finish( );
                };
                
                Consumer<Throwable> onError = throwable -> {
                    Log.e("ERROR", "login", throwable);
                    binding.warningText.setText("구글 회원가입 실패");
                };
                
                var request = new GoogleLoginRequest(googleAccount.getIdToken( ));
                viewModel.googleLogin(request, onComplete, onError);
            }
        }
        catch (ApiException e) {
            e.printStackTrace( );
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
        overridePendingTransition(R.anim.anim_slide_right_to_left_enter, R.anim.anim_slide_right_to_left_exit);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime > 2000) {
                viewModel.makeSnackbar("뒤로가기 버튼을 누르면 앱이 종료됩니다.");
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