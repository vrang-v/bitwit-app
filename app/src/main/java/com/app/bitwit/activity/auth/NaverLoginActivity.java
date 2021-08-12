package com.app.bitwit.activity.auth;

import android.os.Bundle;
import android.webkit.WebSettings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityNaverLoginBinding;
import com.app.bitwit.util.HttpWebViewClient;

public class NaverLoginActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNaverLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_naver_login);
        
        binding.webView.setWebViewClient(new HttpWebViewClient( ));
        
        WebSettings webSettings = binding.webView.getSettings( );
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        
        binding.webView.loadUrl("http://bitwit.site/login/oauth2/authorize/google");
    }
}
