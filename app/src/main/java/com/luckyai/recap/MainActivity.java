package com.luckyai.recap;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.graphics.Color;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        setContentView(webView);

        webView.loadData(
            "<html><body style='font-family:sans-serif;text-align:center;padding:40px'>" +
            "<h2>🎬 Lucky Ai Recap</h2>" +
            "<p>Website ချိတ်ဆက်ရန် အသင့်ဖြစ်နေပါပြီ။</p>" +
            "<p>Public Website တင်ပြီးနောက် ချိတ်ဆက်ပေးပါမည်။</p>" +
            "</body></html>",
            "text/html",
            "UTF-8"
        );
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
