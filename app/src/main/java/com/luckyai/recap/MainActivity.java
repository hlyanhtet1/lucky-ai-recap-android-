package com.luckyai.recap;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
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
        webView.setOnTouchListener((v, event) -> { if (event.getAction() == MotionEvent.ACTION_DOWN) Log.e("LuckyAI-TOUCH", "WebView touched"); return false; });

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new LocalAiBridge(this), "LocalAI");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.e("LuckyAI-JS", message.message() + " at line " + message.lineNumber());
                return true;
            }

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
 --bg:#071B35; --cyan:#00E5FF; --orange:#FF8A00;
 --white:#FFFFFF; --card:#102D4D; --muted:#A9C1D8;
}
* {box-sizing:border-box}
body {
 margin:0; font-family:Arial,sans-serif; color:var(--white);
 background:radial-gradient(circle at top right,#12456B 0%,#071B35 48%,#041326 100%);
 min-height:100vh; padding-bottom:85px;
}
header {padding:21px 19px 12px}
.brand {font-size:23px;font-weight:800;letter-spacing:.3px}
.brand span {color:var(--cyan)}
.subtitle {color:var(--muted);font-size:11px;margin-top:5px}
main {padding:8px 17px 25px}
.page {display:none;animation:fade .2s ease}
.page.active {display:block}
@keyframes fade {from{opacity:.3;transform:translateY(5px)}to{opacity:1;transform:none}}
h2 {font-size:21px;margin:9px 0}
h3 {font-size:16px;margin:4px 0 10px}
p {color:var(--muted);font-size:13px;line-height:1.6}
.card {
 background:linear-gradient(145deg,rgba(22,61,94,.97),rgba(10,35,62,.97));
 border:1px solid rgba(0,229,255,.22);border-radius:18px;
 padding:16px;margin:12px 0;box-shadow:0 8px 24px #0003;
}
.hero {
 padding:20px;border-radius:21px;
 background:linear-gradient(135deg,#075078,#102D4D 60%,#173C5A);
 border:1px solid #00E5FF66;margin-bottom:16px;
}
.heroIcon {font-size:35px}
.toolGrid {display:grid;grid-template-columns:1fr 1fr;gap:11px}
.tool {
 min-height:125px;text-align:left;padding:15px 13px;
 border-radius:17px;border:1px solid #00E5FF44;
 background:linear-gradient(145deg,#163D5E,#0A2541);
 color:white;
}
.tool:active {border-color:var(--cyan);background:#17476A}
.toolIcon {font-size:29px;margin-bottom:10px}
.toolName {font-size:14px;font-weight:800;line-height:1.4}
.toolDesc {font-size:11px;color:var(--muted);margin-top:5px;line-height:1.4}
.btn {
 width:100%;border:0;border-radius:13px;padding:14px;
 margin-top:10px;font-size:14px;font-weight:800;
 background:linear-gradient(90deg,#00BFD8,#00E5FF);color:#04243B;
}
.btn.orange {background:linear-gradient(90deg,#FF8A00,#FFC04D);color:#291500}
.btn.outline {background:transparent;color:var(--cyan);border:1px solid var(--cyan)}
.field {
 width:100%;padding:13px;border-radius:11px;margin:7px 0 13px;
 background:#071B35;color:white;border:1px solid #286080;font-size:14px;
}
.label {font-size:12px;color:var(--cyan);font-weight:bold}
.option {
 padding:12px;border:1px solid #286080;border-radius:12px;
 margin:8px 0;background:#0A2541;color:white;
}
.status {color:var(--cyan);font-size:12px;margin-top:9px}
.small {font-size:11px;color:var(--muted);line-height:1.5}
.step {display:flex;gap:12px;align-items:center;padding:12px 0;border-bottom:1px solid #24445F}
.number {
 width:29px;height:29px;border-radius:50%;display:grid;place-items:center;
 background:#0B536E;color:var(--cyan);font-weight:bold;flex-shrink:0;
}
.preview {
 min-height:145px;border-radius:15px;background:#041326;
 border:1px dashed #36718F;display:grid;place-items:center;
 color:var(--muted);text-align:center;padding:15px;
}
.badge {display:inline-block;padding:5px 9px;border-radius:20px;background:#075078;color:#00E5FF;font-size:10px}
nav {
 position:fixed;bottom:0;left:0;right:0;z-index:5;
 display:flex;justify-content:space-around;
 background:rgba(4,19,38,.98);border-top:1px solid #24506C;
 padding:8px 3px calc(8px + env(safe-area-inset-bottom));
}
nav button {flex:1;border:0;background:none;color:#9BB4C9;font-size:11px;padding:5px 1px}
nav button.active {color:var(--cyan)}
nav b {display:block;font-size:20px;margin-bottom:4px}
</style>
</head>
<body>
<div style="position:fixed;top:0;left:0;right:0;z-index:99999;background:#ff9800;color:#000;padding:12px;text-align:center;font-weight:bold">HTML TEST - NEW APK</div>
<div style="position:fixed;top:0;left:0;right:0;z-index:99999;background:#ff9800;color:#000;padding:12px;text-align:center;font-weight:bold">HTML TEST - NEW APK</div>
<header>
 <div class="brand">LUCKY <span>AI</span> RECAP</div>
 <div class="subtitle">AI VIDEO TOOLS & CREATION STUDIO</div>
</header>

<main>
<section id="home" class="page active">
 <div class="hero">
  <div class="heroIcon">🎬</div>
  <h2>Welcome to Lucky Ai Recap</h2>
  <p>AI Video Tools များကို တစ်နေရာတည်းမှာ အသုံးပြုနိုင်ပါပြီ။ စတင်ရန် Tool တစ်ခုကို ရွေးချယ်ပါ။</p>
  <span class="badge">6 VIDEO TOOLS</span>
 </div>

 <h3>✨ Explore Tools</h3>
 <div class="toolGrid">
  <button class="tool" onclick="openTool('recap')">
   <div class="toolIcon">🎬</div>
   <div class="toolName">Recap</div>
   <div class="toolDesc">Movie Recap ပြုလုပ်ရန်</div>
  </button>
  <button class="tool" onclick="openTool('translate')">
   <div class="toolIcon">🌐</div>
   <div class="toolName">Full Translate & Dubbing</div>
   <div class="toolDesc">ဘာသာပြန်ပြီး အသံထည့်ရန်</div>
  </button>
  <button class="tool" onclick="openTool('cartoon')">
   <div class="toolIcon">🍎</div>
   <div class="toolName">AI Cartoon Creator</div>
   <div class="toolDesc">ကိုယ်ပိုင် Cartoon Video ဖန်တီးရန်</div>
  </button>
  <button class="tool" onclick="openTool('tts')">
   <div class="toolIcon">🎙️</div>
   <div class="toolName">TTS</div>
   <div class="toolDesc">စာသားမှ အသံထုတ်ရန်</div>
  </button>
  <button class="tool" onclick="openTool('subtitle')">
   <div class="toolIcon">💬</div>
   <div class="toolName">Sub ထုတ်ယူရန်</div>
   <div class="toolDesc">Subtitle ထုတ်ယူရန်</div>
  </button>
  <button class="tool" onclick="openTool('download')">
   <div class="toolIcon">⬇️</div>
   <div class="toolName">Video Download</div>
   <div class="toolDesc">Video Download Tool</div>
  </button>
 </div>
 <div class="card">
  <h3>⚡ Local AI Engine</h3>
  <div id="engineStatus" class="status">စစ်ဆေးနေသည်...</div>
  <p class="small">ဖုန်းပေါ်ရှိ Local AI Engine အခြေအနေ</p>
 </div>
</section>

<section id="tool" class="page">
 <h2>🧰 Tool</h2>
 <p id="toolIntro">အသုံးပြုလိုသော Tool ကို ရွေးချယ်ပါ။</p>
 <div class="card">
  <div id="toolIcon" style="font-size:38px">🎬</div>
  <h2 id="toolTitle">Recap</h2>
  <p id="toolDescription">Movie Recap ပြုလုပ်ရန်</p>
  <div id="toolControls"></div>
 </div>
 <button class="btn outline" onclick="go('home')">← Home သို့ပြန်ရန်</button>
</section>

<section id="project" class="page">
 <h2>📁 Projects</h2>
 <p>သင့် Recap လုပ်ငန်း Project များနှင့် လုပ်ငန်းစဉ်များကို ဒီနေရာမှာ စီမံပါမယ်။</p>
 <div class="card">
  <h3>📂 Project Management</h3>
  <div class="project-tabs">
   <button class="btn" onclick="showProjects('Recent')">Recent</button>
   <button class="btn" onclick="showProjects('Processing')">Processing</button>
   <button class="btn" onclick="showProjects('Completed')">Completed</button>
   <button class="btn" onclick="showProjects('Failed')">Failed</button>
  </div>
  <div id="projectList" class="preview">Project များ မရှိသေးပါ။</div>
 </div>
 <div class="card">
  <h3>🎬 Recap Project Workflow</h3>
  <div class="step"><span class="number">1</span><div><b>Video Analysis</b><p>ရွေးထားသော Video ကို စစ်ဆေးခြင်း</p></div></div>
  <div class="step"><span class="number">2</span><div><b>Audio Transcription</b><p>အသံမှ Transcript နှင့် Timestamp ထုတ်ခြင်း</p></div></div>
  <div class="step"><span class="number">3</span><div><b>AI Recap Generation</b><p>Transcript ကို အခြေခံပြီး Recap ရေးသားခြင်း</p></div></div>
  <div class="step"><span class="number">4</span><div><b>Recap Result</b><p>ရရှိလာသော Recap စာသားကို ပြသခြင်း</p></div></div>
  <div class="step"><span class="number">5</span><div><b>Video Edited</b><p>အသံ၊ Subtitle နှင့် Video တည်းဖြတ်ခြင်း</p></div></div>
  <div class="step"><span class="number">6</span><div><b>Export</b><p>နောက်ဆုံး Video ထုတ်ယူခြင်း</p></div></div>
  <p class="status">Workflow UI — Processing Engine ချိတ်ဆက်ရန် ကျန်ရှိသည်။</p>
 </div>
 <div class="card">
  <h3>📝 Recap Result</h3>
  <div class="preview">Recap ရလဒ်ကို ဒီနေရာမှာ ပြသပါမယ်။</div>
 </div>
 <div class="card">
  <h3>🎞️ Video Edited</h3>
  <div class="preview">Edited Video Preview</div>
  <div class="option">✂️ Trim & Cut</div>
  <div class="option">💬 Add Subtitles</div>
  <div class="option">🎙️ Voice Over</div>
  <div class="option">🎵 Background Music</div>
 </div>
 <div class="card">
  <h3>📤 Export</h3>
  <label class="label">Video Quality</label>
  <select class="field"><option>1080p — Full HD</option><option>720p — HD</option><option>480p</option></select>
  <label class="label">Format</label>
  <select class="field"><option>MP4</option></select>
  <button class="btn orange" onclick="alert('Export Engine ကို နောက်အဆင့်မှာ ချိတ်ဆက်ပါမယ်။')">Export Video</button>
 </div>
</section>

<section id="settings" class="page">
 <h2>⚙️ Settings</h2>
 <p>Server၊ API Key နှင့် Recap ပုံစံများကို သတ်မှတ်ပါ။</p>
 <div class="card">
  <h3>🌐 Processing Server</h3>
  <label class="label">Server ရွေးချယ်ရန်</label>
  <select id="server" class="field" onchange="saveSettings()">
   <option value="local">Server 1 — Local AI</option>
   <option value="assemblyai">Server 2 — AssemblyAI API</option>
  </select>
  <p class="small">ရွေးချယ်ထားသော Server ကို Transcription လုပ်ငန်းအတွက် အသုံးပြုရန် ရည်ရွယ်ထားသည်။</p>
  <div id="serverInfo" class="status">Local AI ကို ရွေးထားသည်။</div>
 </div>
 <div class="card">
  <h3>🔑 API Configuration</h3>
  <label class="label">AssemblyAI API Key</label>
  <input id="assemblyKey" class="field" type="password" placeholder="AssemblyAI API Key ထည့်ပါ">
  <label class="label">TTS API Key</label>
  <input id="ttsKey" class="field" type="password" placeholder="TTS API Key ထည့်ပါ">
  <button class="btn" onclick="apiKeyNotice()">Save API Keys</button>
  <p class="small">လုံခြုံသော API Key သိမ်းဆည်းမှုနှင့် Server ချိတ်ဆက်မှုကို နောက်အဆင့်တွင် အကောင်အထည်ဖော်မည်။ လက်ရှိတွင် Key များကို မသိမ်းဆည်းသေးပါ။</p>
  <div id="apiMsg" class="status"></div>
 </div>
 <div class="card">
  <h3>🗣️ Recap Language</h3>
  <select id="language" class="field" onchange="saveSettings()">
   <option>မြန်မာ</option><option>English</option>
  </select>
  <h3>✍️ Recap Style</h3>
  <select id="style" class="field" onchange="saveSettings()">
   <option>Movie Storytelling</option>
   <option>Short & Exciting</option>
   <option>Detailed Recap</option>
  </select>
  <button class="btn orange" onclick="saveSettings()">Save Settings</button>
  <div id="settingsMsg" class="status"></div>
 </div>
</section>
</main>

<nav>
 <button class="active" data-page="home" onclick="go('home')"><b>⌂</b>Home</button>
 <button data-page="tool" onclick="go('tool')"><b>🧰</b>Tool</button>
 <button data-page="project" onclick="go('project')"><b>▣</b>Project</button>
 <button data-page="settings" onclick="go('settings')"><b>⚙</b>Setting</button>
</nav>

<input id="videoFile" type="file" accept="video/*" style="display:none" onchange="chooseVideo(this)">

<script>
window.onerror = function(message, source, line, column) {
  var box = document.getElementById('debugError');
  if (!box) {
    box = document.createElement('div');
    box.id = 'debugError';
    box.style.cssText = 'position:fixed;top:0;left:0;right:0;z-index:99999;background:#b00020;color:white;padding:12px;font-size:14px;white-space:pre-wrap';
    document.body.appendChild(box);
  }
  box.textContent = 'JAVASCRIPT ERROR: ' + message + ' | line: ' + line;
  try { LocalAI.logToolClick('JS ERROR: ' + message + ' line ' + line); } catch(e) {}
  return false;
};
window.addEventListener('DOMContentLoaded', function() {
  var box = document.createElement('div');
  box.textContent = 'JAVASCRIPT RUNNING';
  box.style.cssText = 'position:fixed;top:0;left:0;right:0;z-index:99998;background:#087f23;color:white;padding:10px;text-align:center;font-weight:bold';
  document.body.appendChild(box);
  try { LocalAI.logToolClick('DOM READY'); } catch(e) {}
});
const toolData = {
 recap:{
  icon:'🎬',title:'Recap',
  desc:'Video ကို ရွေးချယ်ပြီး Transcript အခြေခံ Movie Recap ပြုလုပ်ရန်။',
  controls:'<label class="label">Video ရွေးချယ်ရန်</label><button class="btn" onclick="pickVideo()">＋ Choose Video</button><div id="selectedVideo" class="status">Video မရွေးရသေးပါ</div><label class="label">Recap Language</label><select class="field"><option>မြန်မာ</option><option>English</option></select><button class="btn orange" onclick="startProject()">Start Recap Project</button>'
 },
 translate:{
  icon:'🌐',title:'Full Translate & Dubbing',
  desc:'Video တစ်ခုလုံးကို ဘာသာပြန်ပြီး ဘာသာပြန်ထားသော အသံဖြင့် Dubbing ပြုလုပ်ရန်။',
  controls:'<label class="label">Video ရွေးချယ်ရန်</label><button class="btn" onclick="pickVideo()">＋ Choose Video</button><div id="selectedVideo" class="status">Video မရွေးရသေးပါ</div><label class="label">Target Language</label><select class="field"><option>မြန်မာ</option><option>English</option></select><button class="btn orange" onclick="startProject()">Start Translate & Dubbing</button>'
 },
 cartoon:{
  icon:'🍎',title:'AI Cartoon Creator',
  desc:'အသီးပုံစံ Cartoon Character များနှင့် ကိုယ်ပိုင် Video ဖန်တီးရန်။',
  controls:'<label class="label">Character Style</label><select class="field"><option>Fruit Cartoon</option><option>3D Cartoon</option><option>2D Cartoon</option></select><label class="label">Video Idea</label><textarea class="field" rows="4" placeholder="ဖန်တီးလိုသော Cartoon ဇာတ်လမ်းကို ရေးပါ"></textarea><button class="btn orange" onclick="alert(&quot;Cartoon Generation Engine ကို နောက်အဆင့်မှာ ချိတ်ဆက်ပါမယ်။&quot;)">Create Cartoon Video</button>'
 },
 tts:{
  icon:'🎙️',title:'Text To Speech (TTS)',
  desc:'စာသားကို AI အသံအဖြစ် ပြောင်းလဲရန်။',
  controls:'<label class="label">Text ထည့်ရန်</label><textarea class="field" rows="5" placeholder="အသံပြောင်းလိုသော စာသားကို ရေးပါ"></textarea><label class="label">Voice Language</label><select class="field"><option>မြန်မာ</option><option>English</option></select><button class="btn orange" onclick="alert(&quot;TTS Engine ကို နောက်အဆင့်မှာ ချိတ်ဆက်ပါမယ်။&quot;)">Generate Voice</button>'
 },
 subtitle:{
  icon:'💬',title:'Subtitle Extraction',
  desc:'Video ထဲမှ စကားပြောစာသားနှင့် Subtitle ထုတ်ယူရန်။',
  controls:'<label class="label">Video ရွေးချယ်ရန်</label><button class="btn" onclick="pickVideo()">＋ Choose Video</button><div id="selectedVideo" class="status">Video မရွေးရသေးပါ</div><button class="btn orange" onclick="alert(&quot;Subtitle Extraction Engine ကို နောက်အဆင့်မှာ ချိတ်ဆက်ပါမယ်။&quot;)">Extract Subtitles</button>'
 },
 download:{
  icon:'⬇️',title:'Video Download',
  desc:'ခွင့်ပြုထားသော Video URL များမှ Download ပြုလုပ်ရန်။',
  controls:'<label class="label">Video URL</label><input class="field" type="url" placeholder="https://..."><button class="btn orange" onclick="alert(&quot;Video Download Engine ကို နောက်အဆင့်မှာ ချိတ်ဆက်ပါမယ်။&quot;)">Download Video</button>'
 }
};
let currentTool='recap';
let selectedVideoName='';

function go(id) {
 document.querySelectorAll('.page').forEach(p=>p.classList.remove('active'));
 document.getElementById(id).classList.add('active');
 document.querySelectorAll('nav button').forEach(b=>b.classList.toggle('active',b.dataset.page===id));
 window.scrollTo(0,0);
}
function openTool(id) {
 try { LocalAI.logToolClick(id); } catch(e) { console.log('Bridge error: '+e.message); }
 alert('openTool clicked: '+id);
 currentTool=id;
 const t=toolData[id];
 document.getElementById('toolIcon').textContent=t.icon;
 document.getElementById('toolTitle').textContent=t.title;
 document.getElementById('toolDescription').textContent=t.desc;
 document.getElementById('toolControls').innerHTML=t.controls;
 go('tool');
}
function pickVideo() {
 document.getElementById('videoFile').click();
}
function chooseVideo(input) {
 if(input.files && input.files.length) {
  selectedVideoName=input.files[0].name;
  const target=document.getElementById('selectedVideo');
  if(target) target.textContent='Selected: '+selectedVideoName;
 }
}
let currentProjectFilter='Recent';

function loadProjects() {
 try {
  return JSON.parse(localStorage.getItem('lucky_projects')||'[]');
 } catch(e) {
  return [];
 }
}

function saveProjects(projects) {
 localStorage.setItem('lucky_projects',JSON.stringify(projects));
}

function showProjects(filter) {
 currentProjectFilter=filter||'Recent';
 const list=document.getElementById('projectList');
 if(!list) return;
 const projects=loadProjects();
 const filtered=currentProjectFilter==='Recent'
  ?projects
  :projects.filter(p=>p.status===currentProjectFilter);
 if(!filtered.length) {
  list.textContent=currentProjectFilter+' Project မရှိသေးပါ။';
  return;
 }
 list.innerHTML=filtered.map(p=>{
  const safe=v=>String(v||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  const id=Number(p.id)||0;
  return '<div class="step"><div><b>'+safe(p.name||'Video')+'</b><p>'+safe(p.createdAt)+' · '+safe(p.status)+'</p><p>'+safe(p.type)+'</p><button class="btn" onclick="reopenProject('+id+')">ပြန်ဖွင့်</button> <button class="btn" onclick="changeProjectStatus('+id+',\'Completed\')">Completed</button> <button class="btn" onclick="changeProjectStatus('+id+',\'Failed\')">Failed</button> <button class="btn" onclick="deleteProject('+id+')">ဖျက်ရန်</button></div></div>';
 }).join('');
}

function changeProjectStatus(id,status) {
 const projects=loadProjects();
 const project=projects.find(p=>Number(p.id)===Number(id));
 if(!project) return;
 project.status=status;
 saveProjects(projects);
 showProjects(currentProjectFilter);
}

function deleteProject(id) {
 if(!confirm('ဒီ Project ကို ဖျက်မှာ သေချာပါသလား။')) return;
 const projects=loadProjects().filter(p=>Number(p.id)!==Number(id));
 saveProjects(projects);
 showProjects(currentProjectFilter);
}

function reopenProject(id) {
 const project=loadProjects().find(p=>Number(p.id)===Number(id));
 if(!project) return;
 selectedVideoName=project.name;
 currentTool=project.tool||Object.keys(toolData).find(k=>toolData[k].title===project.type)||'recap';
 const t=toolData[currentTool];
 document.getElementById('toolIcon').textContent=t.icon;
 document.getElementById('toolTitle').textContent=t.title;
 document.getElementById('toolDescription').textContent=t.desc;
 document.getElementById('toolControls').innerHTML=t.controls;
 const target=document.getElementById('selectedVideo');
 if(target) target.textContent='Selected: '+selectedVideoName;
 go('tool');
}

function startProject() {
 if(!selectedVideoName) {
  alert('အရင်ဆုံး Video ရွေးချယ်ပါ။');
  return;
 }
 const projects=loadProjects();
 const t=toolData[currentTool];
 projects.unshift({
  id:Date.now(),
  name:selectedVideoName,
  type:t?t.title:currentTool,
  tool:currentTool,
  status:'Processing',
  createdAt:new Date().toLocaleString()
 });
 saveProjects(projects);
 go('project');
 showProjects('Recent');
}
function updateEngineStatus() {
 try {
  document.getElementById('engineStatus').textContent='Local AI Engine: '+LocalAI.getState();
 } catch(e) {
  document.getElementById('engineStatus').textContent='Local AI Engine: '+e.message;
 }
}
function saveSettings() {
 try {
  localStorage.setItem('lucky_server',document.getElementById('server').value);
  localStorage.setItem('lucky_language',document.getElementById('language').value);
  localStorage.setItem('lucky_style',document.getElementById('style').value);
  document.getElementById('settingsMsg').textContent='Settings saved';
  document.getElementById('serverInfo').textContent=
   document.getElementById('server').value==='local'
   ?'Local AI ကို ရွေးထားသည်။'
   :'AssemblyAI API ကို ရွေးထားသည်။ API ချိတ်ဆက်မှု မပြီးသေးပါ။';
 } catch(e) {
  document.getElementById('settingsMsg').textContent=e.message;
 }
}
function apiKeyNotice() {
 document.getElementById('apiMsg').textContent=
 'API Key သိမ်းဆည်းမှုကို လုံခြုံသော Android Storage နှင့် ချိတ်ဆက်ရန် ကျန်ရှိသည်။';
}
try {
 document.getElementById('server').value=localStorage.getItem('lucky_server')||'local';
 document.getElementById('language').value=localStorage.getItem('lucky_language')||'မြန်မာ';
 document.getElementById('style').value=localStorage.getItem('lucky_style')||'Movie Storytelling';
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
