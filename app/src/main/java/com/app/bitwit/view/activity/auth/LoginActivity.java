package com.app.bitwit.view.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.data.source.remote.dto.request.GoogleLoginRequest;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.databinding.ActivityLoginBinding;
import com.app.bitwit.view.activity.FrameActivity;
import com.app.bitwit.viewmodel.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.google.GoogleSignInUtils.getGoogleSignInAccount;
import static com.app.bitwit.util.google.GoogleSignInUtils.getGoogleSignInIntent;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    
    private ActivityLoginBinding binding;
    private LoginViewModel       viewModel;
    
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new StartActivityForResult( ), result -> {
                var account = getGoogleSignInAccount(result.getData( ));
                resolveGoogleSignedInAccount(account);
            }
    );
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        observe(this, viewModel.getSnackbar( ),
                s -> Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        
        binding.confirmBtn.setOnClickListener(v ->
                viewModel.login( )
                         .onSuccess(this::handleLoginSuccess)
                         .onError(this::handleLoginError)
                         .subscribe( )
        );
        
        binding.signUpText.setOnClickListener(v -> {
            var intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_to_right_enter, R.anim.slide_left_to_right_exit);
        });
        
        binding.forgotPasswordText.setOnClickListener(v -> {
            var intent = new Intent(this, PasswordRetrievalActivity.class)
                    .putExtra(ExtraKey.EMAIL, viewModel.getEmail( ).getValue( ));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_left_enter, R.anim.slide_right_to_left_exit);
        });
        
        binding.googleLogin.setOnClickListener(
                v -> googleSignInLauncher.launch(getGoogleSignInIntent(this))
        );
    }
    
    private void handleLoginSuccess(LoginResponse loginResponse) {
        Intent intent;
        if (loginResponse.isEmailVerified( )) {
            viewModel.setSnackbar("???????????????");
            intent = new Intent(this, FrameActivity.class);
        }
        else {
            intent = new Intent(this, EmailVerifyingActivity.class);
        }
        startActivity(intent);
    }
    
    private void handleLoginError(Throwable e) {
        viewModel.setSnackbar("????????? ??????");
        viewModel.setWarningMessage("????????? ?????? ??????????????? ???????????? ?????????\n??????????????? ???????????? ?????? ????????????");
    }
    
    private void resolveGoogleSignedInAccount(GoogleSignInAccount googleAccount) {
        if (googleAccount == null) { return; }
        var request = new GoogleLoginRequest(googleAccount.getIdToken( ));
        viewModel.googleLogin(request)
                 .onSuccess(this::handleLoginSuccess)
                 .onError(this::handleLoginError)
                 .subscribe( );
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_login);
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
                viewModel.setInfoMessage("????????? ????????? ???????????????");
            }
            if (extras.getBoolean(ExtraKey.LOGOUT)) {
                Snackbar.make(binding.getRoot( ), "???????????? ???????????????", LENGTH_SHORT).show( );
            }
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime < 2000) {
                finish( );
            }
            backButtonKeyDownTime = System.currentTimeMillis( );
            viewModel.setSnackbar("???????????? ????????? ????????? ?????? ???????????????.");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
