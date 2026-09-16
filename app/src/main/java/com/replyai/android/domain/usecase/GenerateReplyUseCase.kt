package com.replyai.android.domain.usecase

import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply

class GenerateReplyUseCase(
    private val aiProvider: AiProvider
) {
    suspend operator fun invoke(request: CommunicationRequest): GeneratedReply {
        require(request.inputText.isNotBlank()) { "Input text must not be blank." }
        return aiProvider.generateReply(request)
    }
}
