package com.example.letsscan;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class GoogleActivity extends AppCompatActivity {
    private String URL;
    private WebView google_search;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_google);
        this.URL = getIntent().getStringExtra("URL");
        WebView webView = (WebView) findViewById(R.id.google_search);
        this.google_search = webView;
        webView.setWebViewClient(new WebViewClient());
        this.google_search.loadUrl(this.URL);
    }
}

