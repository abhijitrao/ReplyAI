package com.replyai.android.domain.ai

import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply

/**
 * Provider-agnostic AI contract. Implementations may use an online API or
 * an on-device model. Domain and presentation code must not depend on either.
 */
interface AiProvider {
    suspend fun generateReply(request: CommunicationRequest): GeneratedReply
}
