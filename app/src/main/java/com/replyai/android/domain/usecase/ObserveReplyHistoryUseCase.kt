package com.replyai.android.domain.usecase

import com.replyai.android.domain.model.ReplyHistoryItem
import com.replyai.android.domain.repository.ReplyHistoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveReplyHistoryUseCase(
    private val repository: ReplyHistoryRepository
) {
    operator fun invoke(): Flow<List<ReplyHistoryItem>> = repository.observeHistory()
}
