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

        String html =
            "<html><head><meta name='viewport' content='width=device-width, initial-scale=1'>" +
            "<style>body{font-family:sans-serif;text-align:center;padding:24px;background:#f7f7fb;color:#222}" +
            "select{width:100%;max-width:300px;padding:11px;border:1px solid #aaa;border-radius:8px;font-size:15px;background:white}" +
            "input{margin-top:28px;font-size:16px}</style></head><body>" +
            "<h2>🎬 Lucky Ai Recap</h2>" +
            "<p style='font-size:13px;color:#666'>Processing Server</p>" +
            "<select id='server' onchange='saveServer()'>" +
            "<option value='local'>Server 1 — Local AI</option>" +
            "<option value='assemblyai'>Server 2 — AssemblyAI</option>" +
            "</select>" +
            "<p id='selected' style='font-size:12px;color:#666'>Server 1 ကို ရွေးထားသည်</p>" +
            "<p>Video ရွေးချယ်ပါ</p>" +
            "<input type='file' accept='video/*'>" +
            "<script>" +
            "const sel=document.getElementById('server');" +
            "try{sel.value=localStorage.getItem('lucky_server')||'local';}catch(e){}" +
            "function updateLabel(){document.getElementById('selected').textContent=sel.value==='local'?'Server 1 ကို ရွေးထားသည်':'Server 2 ကို ရွေးထားသည်';}" +
            "function saveServer(){try{localStorage.setItem('lucky_server',sel.value);}catch(e){}updateLabel();}" +
            "updateLabel();" +
            "</script></body></html>";

        webView.loadDataWithBaseURL(
            "https://lucky-ai.local/",
            html,
            "text/html",
            "UTF-8",
            null
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
