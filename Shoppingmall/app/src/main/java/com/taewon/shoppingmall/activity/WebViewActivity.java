package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.internal.ImagesContract;
import com.taewon.shoppingmall.R;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_webview);
        WebView webView2 = (WebView) findViewById(R.id.wv_webview);
        this.webView = webView2;
        webView2.setWebViewClient(new WebViewClient());
        this.webView.loadUrl(getIntent().getStringExtra(ImagesContract.URL));
    }
}