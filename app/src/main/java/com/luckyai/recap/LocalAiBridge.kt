package com.luckyai.recap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.arm.aichat.AiChat
import com.arm.aichat.InferenceEngine
import org.json.JSONObject

class LocalAiBridge(private val context: Context, private val webView: WebView) {
    private val engine = AiChat.getInferenceEngine(context.applicationContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        const val MODEL_REQUEST = 1002
    }

    @JavascriptInterface
    fun logToolClick(id: String) {
        Log.e("LuckyAI-BRIDGE", "Tool clicked: $id")
    }

    @JavascriptInterface
    fun getState(): String {
        return when (val state = engine.state.value) {
            is InferenceEngine.State.Error -> "Error: ${state.exception.message}"
            else -> state.javaClass.simpleName
        }
    }

    @JavascriptInterface
    fun pickModel() {
        val activity = context as? Activity ?: return
        activity.runOnUiThread {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/octet-stream",
                    "application/x-gguf"
                ))
            }
            activity.startActivityForResult(intent, MODEL_REQUEST)
        }
    }

    fun handleModelResult(uri: Uri?) {
        if (uri == null) {
            showMessage("Model ဖိုင် မရွေးချယ်ခဲ့ပါ")
            return
        }

        scope.launch {
            try {
                showMessage("Model ဖိုင် ကူးယူနေသည်...")
                val modelFile = withContext(Dispatchers.IO) {
                    ModelStorage.copyModel(context, uri)
                }

                showMessage("AI Engine စတင်ရန် ပြင်ဆင်နေသည်...")
                val state = withContext(Dispatchers.IO) {
                    engine.state.first {
                        it is InferenceEngine.State.Initialized ||
                        it is InferenceEngine.State.Error
                    }
                }
                if (state is InferenceEngine.State.Error) {
                    throw state.exception
                }

                showMessage("Local AI Model Load လုပ်နေသည်...")
                engine.loadModel(modelFile.absolutePath)
                engine.setSystemPrompt(
                    "You are Lucky Ai Recap, an expert movie storyteller. " +
                    "Create accurate and engaging movie recaps in Burmese. " +
                    "Do not invent events that are not in the source."
                )
                showMessage("Local AI Model အသင့်ဖြစ်ပါပြီ")
            } catch (e: Exception) {
                Log.e("LuckyAI-BRIDGE", "Model loading failed", e)
                showMessage("Model Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    private fun showMessage(message: String) {
        val script = "var e=document.getElementById('modelStatus');" +
            "if(e)e.textContent=${JSONObject.quote(message)};"
        (context as? Activity)?.runOnUiThread {
            webView.evaluateJavascript(script, null)
        }
    }

    fun close() {
        scope.cancel()
    }
}
