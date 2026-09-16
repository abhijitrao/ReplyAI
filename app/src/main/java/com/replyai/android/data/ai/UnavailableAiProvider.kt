package com.replyai.android.data.ai

import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply

/**
 * Keeps the application usable when an AI provider is not configured.
 * It never returns a fake reply; generation fails with a user-facing reason.
 */
class UnavailableAiProvider(
    private val reason: String
) : AiProvider {

    override suspend fun generateReply(request: CommunicationRequest): GeneratedReply {
        throw IllegalStateException(reason)
    }
}
