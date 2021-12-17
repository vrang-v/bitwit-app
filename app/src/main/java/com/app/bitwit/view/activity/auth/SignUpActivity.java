package com.app.bitwit.view.activity.auth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.databinding.ActivitySignUpBinding;
import com.app.bitwit.util.google.GoogleSignInUtils;
import com.app.bitwit.view.activity.FrameActivity;
import com.app.bitwit.viewmodel.SignUpViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.StringUtils.isEmailFormat;
import static com.app.bitwit.util.google.GoogleSignInUtils.getGoogleSignInAccount;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.app.bitwit.view.activity.auth.SignUpProcessActivity.RESULT_FAILURE;
import static com.app.bitwit.view.activity.auth.SignUpProcessActivity.RESULT_SUCCESS;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {
    
    private ActivitySignUpBinding binding;
    private SignUpViewModel       viewModel;
    
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new StartActivityForResult( ), result -> {
                var account = getGoogleSignInAccount(result.getData( ));
                resolveGoogleSignedInAccount(account);
            }
    );
    
    private final ActivityResultLauncher<Intent> signInProcessLauncher = registerForActivityResult(
            new StartActivityForResult( ), result -> {
                int resultCode = result.getResultCode( );
                if (resultCode == RESULT_SUCCESS) {
                    var intent = new Intent(getApplicationContext( ), LoginActivity.class)
                            .putExtra(ExtraKey.EMAIL, viewModel.getEmail( ).getValue( ))
                            .putExtra(ExtraKey.PASSWORD, viewModel.getPassword( ).getValue( ))
                            .putExtra(ExtraKey.SIGN_IN_SUCCESS, true);
                    startActivity(intent);
                    finish( );
                }
                if (resultCode == RESULT_FAILURE) {
                    viewModel.setSnackbar("회원 가입 도중 오류가 발생했습니다");
                }
            }
    );
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        observe(this, viewModel.getSnackbar( ), s ->
                Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        
        observe(this, viewModel.getEmail( ), email -> {
            if (isEmailFormat(email)) {
                viewModel.initMessage( );
            }
            else {
                viewModel.setInfoMessage("올바른 이메일 형식인가요?\n다시 한번 확인해 주세요");
            }
        });
        
        binding.signUpButton.setOnClickListener(v ->
                viewModel.checkForDuplicateEmail( )
                         .onSuccess(isDuplicate -> {
                             if (isDuplicate == null) {
                                 viewModel.setSnackbar("인증 도중 문제가 발생했습니다. 다시 시도해주세요.");
                                 return;
                             }
                             if (isDuplicate) {
                                 viewModel.setWarningMessage("이미 가입된 이메일이 있습니다.");
                             }
                             else {
                                 viewModel.initMessage( );
                                 var intent = new Intent(this, SignUpProcessActivity.class)
                                         .putExtra(ExtraKey.EMAIL, viewModel.getEmail( ).getValue( ))
                                         .putExtra(ExtraKey.PASSWORD, viewModel.getPassword( ).getValue( ));
                                 signInProcessLauncher.launch(intent);
                             }
                         })
                         .onError(e ->
                                 viewModel.setWarningMessage("서버에 문제가 발생했습니다\n잠시 후 다시 시도해주세요")
                         )
                         .subscribe( )
        );
        
        binding.loginText.setOnClickListener(v -> {
            var intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish( );
        });
        
        binding.googleLogin.setOnClickListener(v ->
                googleSignInLauncher.launch(GoogleSignInUtils.getGoogleSignInIntent(this))
        );
    }
    
    private void resolveGoogleSignedInAccount(GoogleSignInAccount googleAccount) {
        if (googleAccount == null) { return; }
        var request = new GoogleLoginRequest(googleAccount.getIdToken( ));
        viewModel.googleLogin(request)
                 .onSuccess(response -> {
                     var intent = new Intent(getApplicationContext( ), FrameActivity.class);
                     startActivity(intent);
                     finish( );
                 })
                 .onError(e -> viewModel.setWarningMessage("구글 회원가입 실패"))
                 .subscribe( );
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime < 2000) {
                finish( );
            }
            backButtonKeyDownTime = System.currentTimeMillis( );
            viewModel.setSnackbar("뒤로가기 버튼을 누르면 앱이 종료됩니다.");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
