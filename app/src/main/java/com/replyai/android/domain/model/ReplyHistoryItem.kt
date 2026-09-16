package com.replyai.android.domain.model

data class ReplyHistoryItem(
    val id: Long,
    val inputText: String,
    val reply: String,
    val translation: String,
    val tone: Tone,
    val responseLength: ResponseLength,
    val createdAt: Long
)
