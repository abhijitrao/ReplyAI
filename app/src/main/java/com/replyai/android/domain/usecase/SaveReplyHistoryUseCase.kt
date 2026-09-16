package com.replyai.android.domain.usecase

import com.replyai.android.domain.model.ReplyHistoryItem
import com.replyai.android.domain.repository.ReplyHistoryRepository

class SaveReplyHistoryUseCase(
    private val repository: ReplyHistoryRepository
) {
    suspend operator fun invoke(item: ReplyHistoryItem) {
        repository.save(item)
    }
}
