package com.replyai.android.presentation.common

import android.content.Context
import android.content.Intent

object ShareText {

    fun share(context: Context, text: String) {
        if (text.isBlank()) return

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share reply")
        )
    }
}
