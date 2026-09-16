package com.replyai.android.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.replyai.android.domain.model.ReplyHistoryItem
import com.replyai.android.domain.model.ResponseLength
import com.replyai.android.domain.model.Tone

@Entity(tableName = "reply_history")
data class ReplyHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val inputText: String,
    val reply: String,
    val translation: String,
    val tone: String,
    val responseLength: String,
    val createdAt: Long
)

fun ReplyHistoryEntity.toDomain(): ReplyHistoryItem = ReplyHistoryItem(
    id = id,
    inputText = inputText,
    reply = reply,
    translation = translation,
    tone = Tone.valueOf(tone),
    responseLength = ResponseLength.valueOf(responseLength),
    createdAt = createdAt
)

fun ReplyHistoryItem.toEntity(): ReplyHistoryEntity = ReplyHistoryEntity(
    id = id,
    inputText = inputText,
    reply = reply,
    translation = translation,
    tone = tone.name,
    responseLength = responseLength.name,
    createdAt = createdAt
)
