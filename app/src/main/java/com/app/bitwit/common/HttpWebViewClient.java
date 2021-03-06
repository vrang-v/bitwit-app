package com.app.bitwit.common;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HttpWebViewClient extends WebViewClient {
    
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed( );
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        System.out.println("request.getUrl().toString() = " + request.getUrl( ).toString( ));
        view.loadUrl(request.getUrl( ).toString( ));
        return true;
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        System.out.println("url = " + url);
        view.loadUrl(url);
        return true;
    }
}
