package com.replyai.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ReplyAiColors = lightColorScheme()

@Composable
fun ReplyAiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ReplyAiColors,
        content = content
    )
}
