
package com.luckyai.recap

import android.content.Context
import android.net.Uri
import java.io.File

object ModelStorage {
    fun copyModel(context: Context, uri: Uri): File {
        val modelDir = File(context.filesDir, "models")
        if (!modelDir.exists() && !modelDir.mkdirs()) {
            throw IllegalStateException("Model folder ဖန်တီးမရပါ")
        }

        val modelFile = File(modelDir, "local-model.gguf")
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Model ဖိုင်ကို ဖတ်မရပါ")

        input.use { source ->
            modelFile.outputStream().use { output ->
                source.copyTo(output)
            }
        }

        if (!modelFile.exists() || modelFile.length() == 0L) {
            throw IllegalStateException("Model ဖိုင် ကူးယူမှု မအောင်မြင်ပါ")
        }

        return modelFile
    }
}
