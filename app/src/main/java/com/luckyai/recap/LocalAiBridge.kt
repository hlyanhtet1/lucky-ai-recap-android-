package com.luckyai.recap

import android.content.Context
import android.webkit.JavascriptInterface
import android.util.Log
import com.arm.aichat.AiChat
import com.arm.aichat.InferenceEngine

class LocalAiBridge(context: Context) {
    private val engine = AiChat.getInferenceEngine(context.applicationContext)

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
}
