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
        webView.setBackgroundColor(Color.rgb(7, 27, 53));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new LocalAiBridge(this), "LocalAI");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                WebView view,
                ValueCallback<Uri[]> callback,
                FileChooserParams params
            ) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
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

        String html = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
:root {
 --bg:#071B35; --cyan:#00E5FF; --orange:#FF8A00; --white:#FFFFFF;
 --card:#102D4D; --muted:#A9C1D8;
}
* { box-sizing:border-box; }
body {
 margin:0; font-family:Arial,sans-serif; color:var(--white);
 background:radial-gradient(circle at top right,#12456B 0%,#071B35 48%,#041326 100%);
 min-height:100vh; padding-bottom:85px;
}
header { padding:22px 20px 12px; }
.brand { font-size:23px; font-weight:800; letter-spacing:.3px; }
.brand span { color:var(--cyan); }
.subtitle { color:var(--muted); font-size:12px; margin-top:6px; }
main { padding:8px 18px 24px; }
.page { display:none; animation:fade .2s ease; }
.page.active { display:block; }
@keyframes fade { from{opacity:.3;transform:translateY(5px)} to{opacity:1;transform:none} }
.card {
 background:linear-gradient(145deg,rgba(22,61,94,.96),rgba(10,35,62,.96));
 border:1px solid rgba(0,229,255,.22); border-radius:20px;
 padding:19px; margin:14px 0;
 box-shadow:0 8px 24px rgba(0,0,0,.18);
}
h2 { font-size:21px; margin:8px 0 7px; }
h3 { font-size:16px; margin:4px 0 10px; }
p { color:var(--muted); font-size:13px; line-height:1.6; }
.hero {
 padding:23px 18px; border-radius:22px;
 background:linear-gradient(135deg,#075078,#102D4D 60%,#173C5A);
 border:1px solid rgba(0,229,255,.4);
}
.heroIcon { font-size:42px; margin-bottom:12px; }
.btn {
 width:100%; border:0; border-radius:14px; padding:15px;
 margin-top:12px; font-size:15px; font-weight:800;
 background:linear-gradient(90deg,#00BFD8,#00E5FF);
 color:#04243B;
}
.btn.orange { background:linear-gradient(90deg,#FF8A00,#FFC04D); color:#291500; }
.btn.outline { background:transparent; color:var(--cyan); border:1px solid var(--cyan); }
.field {
 width:100%; padding:13px; border-radius:12px; margin:7px 0 12px;
 background:#071B35; color:white; border:1px solid #286080; font-size:14px;
}
.option {
 padding:13px; border:1px solid #286080; border-radius:13px;
 margin:9px 0; color:white; background:#0A2541;
}
.option input { accent-color:#00E5FF; margin-right:9px; }
.status { color:var(--cyan); font-size:12px; margin-top:10px; }
.step { display:flex; gap:12px; align-items:center; padding:13px 0; border-bottom:1px solid #24445F; }
.number {
 width:30px;height:30px;border-radius:50%;display:grid;place-items:center;
 background:#0B536E;color:var(--cyan);font-weight:bold;flex-shrink:0;
}
.muted { color:var(--muted); }
.preview {
 height:170px; border-radius:16px; background:#041326;
 border:1px dashed #36718F; display:grid; place-items:center;
 color:var(--muted); text-align:center; padding:15px;
}
nav {
 position:fixed; bottom:0; left:0; right:0; z-index:5;
 display:flex; justify-content:space-around; gap:2px;
 background:rgba(4,19,38,.97); border-top:1px solid #24506C;
 padding:9px 3px calc(9px + env(safe-area-inset-bottom));
}
nav button {
 flex:1; border:0; background:none; color:#9BB4C9;
 font-size:10px; padding:5px 1px;
}
nav button.active { color:var(--cyan); }
nav b { display:block; font-size:19px; margin-bottom:4px; }
.small { font-size:11px; color:var(--muted); }
</style>
</head>
<body>
<header>
 <div class="brand">LUCKY <span>AI</span> RECAP</div>
 <div class="subtitle">AI VIDEO RECAP STUDIO</div>
</header>

<main>
<section id="home" class="page active">
 <div class="hero">
  <div class="heroIcon">🎬</div>
  <h2>Turn Movies Into Stories</h2>
  <p>သင့် Video ကို AI နဲ့ Recap ပြုလုပ်ပြီး ကြည့်ရှုရလွယ်ကူတဲ့ Story အဖြစ် ပြောင်းလဲပါ။</p>
  <button class="btn" onclick="document.getElementById('videoFile').click()">＋ Choose Video</button>
  <input id="videoFile" type="file" accept="video/*" style="display:none" onchange="chooseVideo(this)">
  <p id="videoName" class="status">Video မရွေးရသေးပါ</p>
 </div>
 <div class="card">
  <h3>⚡ Local AI Engine</h3>
  <div id="engineStatus" class="status">စစ်ဆေးနေသည်...</div>
  <p>သင့်ဖုန်းပေါ်မှာ Local AI Engine အခြေအနေ</p>
 </div>
 <div class="card">
  <h3>How Recap Works</h3>
  <div class="step"><span class="number">1</span><span>Video ရွေးချယ်ခြင်း</span></div>
  <div class="step"><span class="number">2</span><span>အသံမှ Transcript ထုတ်ခြင်း</span></div>
  <div class="step"><span class="number">3</span><span>AI နဲ့ Recap ရေးသားခြင်း</span></div>
  <div class="step"><span class="number">4</span><span>Video တည်းဖြတ်ခြင်း</span></div>
  <button class="btn orange" onclick="go('processing')">View Workflow →</button>
 </div>
</section>

<section id="settings" class="page">
 <h2>⚙️ Settings</h2>
 <p>Recap ပြုလုပ်မယ့်ပုံစံကို ရွေးချယ်ပါ။</p>
 <div class="card">
  <h3>Processing Server</h3>
  <select id="server" class="field" onchange="saveSettings()">
   <option value="local">Server 1 — Local AI</option>
   <option value="assemblyai">Server 2 — AssemblyAI</option>
  </select>
  <div class="small">Local AI သည် ဖုန်းပေါ်တွင် အလုပ်လုပ်ရန် ရည်ရွယ်ထားသည်။</div>
 </div>
 <div class="card">
  <h3>Recap Language</h3>
  <select id="language" class="field" onchange="saveSettings()">
   <option>မြန်မာ</option><option>English</option>
  </select>
  <h3>Recap Style</h3>
  <select id="style" class="field" onchange="saveSettings()">
   <option>Movie Storytelling</option>
   <option>Short & Exciting</option>
   <option>Detailed Recap</option>
  </select>
  <button class="btn" onclick="saveSettings()">Save Settings</button>
  <div id="settingsMsg" class="status"></div>
 </div>
</section>

<section id="processing" class="page">
 <h2>⚡ Processing Workflow</h2>
 <p>Recap လုပ်ငန်းစဉ် အဆင့်များ</p>
 <div class="card">
  <div class="step"><span class="number">1</span><div><b>Video Analysis</b><p>Video ကို စစ်ဆေးခြင်း</p></div></div>
  <div class="step"><span class="number">2</span><div><b>Audio Transcription</b><p>အသံမှ စာသားနှင့် အချိန်မှတ်တမ်း ထုတ်ခြင်း</p></div></div>
  <div class="step"><span class="number">3</span><div><b>AI Recap Generation</b><p>Transcript ကို အသုံးပြုပြီး Recap ရေးသားခြင်း</p></div></div>
  <div class="step"><span class="number">4</span><div><b>Video Editing</b><p>Recap အတွက် Video တည်းဖြတ်ခြင်း</p></div></div>
  <p class="status">လုပ်ဆောင်ချက်များကို အဆင့်လိုက် ချိတ်ဆက်ရန် အသင့်ဖြစ်နေသည်။</p>
 </div>
 <button class="btn" onclick="go('result')">Continue to Recap Result →</button>
 <p class="small">မှတ်ချက် — လက်ရှိတွင် Workflow UI ကို ပြသထားခြင်းဖြစ်ပြီး အမှန်တကယ် Processing ကို မချိတ်ဆက်ရသေးပါ။</p>
</section>

<section id="result" class="page">
 <h2>📝 Recap Result</h2>
 <div class="card">
  <h3>Your Movie Recap</h3>
  <div class="preview">AI Recap စာသားကို ဒီနေရာမှာ ပြသပါမယ်။</div>
  <button class="btn outline" onclick="go('edit')">Go to Video Editing →</button>
 </div>
</section>

<section id="edit" class="page">
 <h2>🎞️ Video Edited</h2>
 <p>တည်းဖြတ်ပြီးသား Video ကို ဒီနေရာမှာ Preview ကြည့်နိုင်ပါမယ်။</p>
 <div class="card">
  <div class="preview">▶<br>Edited Video Preview</div>
  <h3>Editing Options</h3>
  <div class="option">✂️ Trim & Cut</div>
  <div class="option">💬 Add Subtitles</div>
  <div class="option">🎙️ Voice Over</div>
  <div class="option">🎵 Background Music</div>
  <button class="btn orange" onclick="go('export')">Continue to Export →</button>
 </div>
</section>

<section id="export" class="page">
 <h2>📤 Export Video</h2>
 <div class="card">
  <div class="preview">🎬<br>Final Video Preview</div>
  <h3>Export Settings</h3>
  <select class="field"><option>1080p — Full HD</option><option>720p — HD</option><option>480p</option></select>
  <select class="field"><option>MP4</option></select>
  <button class="btn orange" onclick="alert('Export Engine ကို နောက်အဆင့်မှာ ချိတ်ဆက်ပါမယ်။')">Export Video</button>
  <p class="small">Export Engine ကို မချိတ်ဆက်ရသေးပါ။</p>
 </div>
</section>
</main>

<nav>
 <button class="active" data-page="home" onclick="go('home')"><b>⌂</b>Home</button>
 <button data-page="settings" onclick="go('settings')"><b>⚙</b>Settings</button>
 <button data-page="processing" onclick="go('processing')"><b>⚡</b>Process</button>
 <button data-page="result" onclick="go('result')"><b>▤</b>Result</button>
 <button data-page="edit" onclick="go('edit')"><b>✂</b>Edit</button>
 <button data-page="export" onclick="go('export')"><b>⇧</b>Export</button>
</nav>

<script>
function go(id) {
 document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
 document.getElementById(id).classList.add('active');
 document.querySelectorAll('nav button').forEach(b => b.classList.toggle('active', b.dataset.page === id));
 window.scrollTo(0,0);
}
function chooseVideo(input) {
 if(input.files && input.files.length) {
  document.getElementById('videoName').textContent = 'Selected: ' + input.files[0].name;
 }
}
function updateEngineStatus() {
 try {
  document.getElementById('engineStatus').textContent = 'Local AI Engine: ' + LocalAI.getState();
 } catch(e) {
  document.getElementById('engineStatus').textContent = 'Engine Error: ' + e.message;
 }
}
function saveSettings() {
 try {
  localStorage.setItem('lucky_server',document.getElementById('server').value);
  localStorage.setItem('lucky_language',document.getElementById('language').value);
  localStorage.setItem('lucky_style',document.getElementById('style').value);
  document.getElementById('settingsMsg').textContent = 'Settings saved';
 } catch(e) {
  document.getElementById('settingsMsg').textContent = e.message;
 }
}
try {
 document.getElementById('server').value = localStorage.getItem('lucky_server') || 'local';
 document.getElementById('language').value = localStorage.getItem('lucky_language') || 'မြန်မာ';
 document.getElementById('style').value = localStorage.getItem('lucky_style') || 'Movie Storytelling';
} catch(e) {}
setInterval(updateEngineStatus,1000);
updateEngineStatus();
</script>
</body>
</html>
""";

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
