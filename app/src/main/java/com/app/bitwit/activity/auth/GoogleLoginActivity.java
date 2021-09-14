package com.app.bitwit.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityGoogleLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

@AndroidEntryPoint
public class GoogleLoginActivity extends AppCompatActivity {
    
    private static final int RC_SIGN_IN = 1;
    
    private GoogleSignInClient googleClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGoogleLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_google_login);
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings( );
        
        var signInOptions = new Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail( )
                .build( );
        googleClient = GoogleSignIn.getClient(this, signInOptions);
        
        binding.signInButton.setOnClickListener(this::onLoginBtnClick);
    }
    
    public void onLoginBtnClick(View view) {
        Intent signInIntent = googleClient.getSignInIntent( );
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            var googleAccount = completedTask.getResult(ApiException.class);
            if (googleAccount != null) {
                var personName       = googleAccount.getDisplayName( );
                var personGivenName  = googleAccount.getGivenName( );
                var personFamilyName = googleAccount.getFamilyName( );
                var personEmail      = googleAccount.getEmail( );
                var personId         = googleAccount.getId( );
                var personPhoto      = googleAccount.getPhotoUrl( );
                
                Log.d("TAG", "handleSignInResult:personName " + personName);
                Log.d("TAG", "handleSignInResult:personGivenName " + personGivenName);
                Log.d("TAG", "handleSignInResult:personEmail " + personEmail);
                Log.d("TAG", "handleSignInResult:personId " + personId);
                Log.d("TAG", "handleSignInResult:personFamilyName " + personFamilyName);
                Log.d("TAG", "handleSignInResult:personPhoto " + personPhoto);
            }
        }
        catch (ApiException e) {
            e.printStackTrace( );
        }
    }
}
