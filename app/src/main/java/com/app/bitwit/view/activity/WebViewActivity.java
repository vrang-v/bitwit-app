package com.app.bitwit.view.activity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityWebViewBinding;
import com.app.bitwit.common.HttpWebViewClient;

public class WebViewActivity extends AppCompatActivity {
    
    public static final String URL = "URL";
    
    private ActivityWebViewBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        
        Bundle extras = getIntent( ).getExtras( );
        
        String url = extras.getString(URL);
        
        loadWebView(url);
    }
    
    private void loadWebView(String url) {
        WebSettings settings = binding.webView.getSettings( );
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setUseWideViewPort(true);
        settings.setBlockNetworkImage(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        
        binding.webView.setWebChromeClient(new WebChromeClient( ));
        binding.webView.setWebViewClient(new HttpWebViewClient( ));
        binding.webView.clearCache(true);
        
        binding.webView.loadUrl(url);
    }
    
    @Override
    public void onBackPressed( ) {
        if (binding.webView.canGoBack( )) {
            binding.webView.goBack( );
            return;
        }
        super.onBackPressed( );
    }
}
