package com.fyp.Beauticianatyourdoorstep.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;

public class MapActivity extends AppCompatActivity implements MyConstants {
    private WebView webview;
    private ProgressBar progressBar;
    private TextView progress_status;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        String city = intent.getStringExtra(EXTRA_CUSTOMER_CITY);
        progressBar = findViewById(R.id.progressbar);
        progress_status = findViewById(R.id.progress_status);
        webview = findViewById(R.id.webview);
        WebSettings webviewSettings = webview.getSettings();
        webviewSettings.setJavaScriptEnabled(true);
        webviewSettings.setBuiltInZoomControls(true);
        webviewSettings.setDisplayZoomControls(false);
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.loadUrl(url);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress_status.setText(newProgress + " %");
                if (view.getVisibility() == View.VISIBLE && newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    progress_status.setVisibility(View.GONE);
                }
            }
        });
        webview.loadUrl("https://www.google.com/maps/place/" + city);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webview != null) {
            webview.destroy();
        }
    }

    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
