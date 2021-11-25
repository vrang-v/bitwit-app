package com.app.bitwit.util.google;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.ApiException;
import lombok.NoArgsConstructor;
import lombok.var;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GoogleSignInUtils {
    
    private static final String CLIENT_ID = "355754127095-5f1mrhut2fo7sitlvo90v07vhdhp992o.apps.googleusercontent.com";
    
    public static GoogleSignInClient getGoogleSignInClient(Activity activity) {
        var signInOptions = new Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail( )
                .build( );
        return GoogleSignIn.getClient(activity, signInOptions);
    }
    
    public static Intent getGoogleSignInIntent(Activity activity) {
        return getGoogleSignInClient(activity).getSignInIntent( );
    }
    
    public static GoogleSignInAccount getGoogleSignInAccount(Intent intent) {
        try {
            return GoogleSignIn.getSignedInAccountFromIntent(intent).getResult(ApiException.class);
        }
        catch (ApiException ignored) { }
        return null;
    }
}
