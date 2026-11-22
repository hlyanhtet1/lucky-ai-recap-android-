package com.luckyai.recap

import android.content.Context
import android.webkit.JavascriptInterface
import com.arm.aichat.AiChat
import com.arm.aichat.InferenceEngine

class LocalAiBridge(context: Context) {
    private val engine = AiChat.getInferenceEngine(context.applicationContext)

    @JavascriptInterface
    fun getState(): String {
        return when (val state = engine.state.value) {
            is InferenceEngine.State.Error -> "Error: ${state.exception.message}"
            else -> state.javaClass.simpleName
        }
    }
}
