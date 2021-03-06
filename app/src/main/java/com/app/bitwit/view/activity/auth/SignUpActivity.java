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
                    viewModel.setSnackbar("?????? ?????? ?????? ????????? ??????????????????");
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
                viewModel.setInfoMessage("????????? ????????? ????????????????\n?????? ?????? ????????? ?????????");
            }
        });
        
        binding.signUpButton.setOnClickListener(v ->
                viewModel.checkForDuplicateEmail( )
                         .onSuccess(isDuplicate -> {
                             if (isDuplicate == null) {
                                 viewModel.setSnackbar("?????? ?????? ????????? ??????????????????. ?????? ??????????????????.");
                                 return;
                             }
                             if (isDuplicate) {
                                 viewModel.setWarningMessage("?????? ????????? ???????????? ????????????.");
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
                                 viewModel.setWarningMessage("????????? ????????? ??????????????????\n?????? ??? ?????? ??????????????????")
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
                 .onError(e -> viewModel.setWarningMessage("?????? ???????????? ??????"))
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
            viewModel.setSnackbar("???????????? ????????? ????????? ?????? ???????????????.");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
