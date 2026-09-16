package com.replyai.android.domain.usecase

import com.replyai.android.domain.repository.ReplyHistoryRepository

class DeleteReplyHistoryUseCase(
    private val repository: ReplyHistoryRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.delete(id)
    }
}
