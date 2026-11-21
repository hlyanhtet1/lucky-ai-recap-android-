package com.luckyai.recap;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.graphics.Color;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                WebView view,
                ValueCallback<Uri[]> callback,
                FileChooserParams params
            ) {
                if (fileCallback != null) {
                    fileCallback.onReceiveValue(null);
                }

                fileCallback = callback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");

                try {
                    startActivityForResult(
                        Intent.createChooser(intent, "Video ရွေးပါ"),
                        FILE_REQUEST
                    );
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }

                return true;
            }
        });

        setContentView(webView);

        webView.loadData(
            "<html><body style='font-family:sans-serif;text-align:center;padding:40px'>" +
            "<h2>🎬 Lucky Ai Recap</h2>" +
            "<p>Video Picker အသင့်ဖြစ်နေပါပြီ။</p>" +
            "<input type=\"file\" accept=\"video/*\" style=\"font-size:18px\">" +
            "<p>ဖုန်းထဲမှ Video ရွေးချယ်စမ်းသပ်ပါ။</p>" +
            "</body></html>",
            "text/html",
            "UTF-8"
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST && fileCallback != null) {
            Uri[] results = null;

            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                results = new Uri[]{data.getData()};
            }

            fileCallback.onReceiveValue(results);
            fileCallback = null;
        }
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
