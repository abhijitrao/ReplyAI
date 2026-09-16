package com.replyai.android.domain.repository

import com.replyai.android.domain.model.ReplyHistoryItem
import kotlinx.coroutines.flow.Flow

interface ReplyHistoryRepository {
    fun observeHistory(): Flow<List<ReplyHistoryItem>>
    suspend fun save(item: ReplyHistoryItem)
    suspend fun delete(id: Long)
    suspend fun clear()
}
