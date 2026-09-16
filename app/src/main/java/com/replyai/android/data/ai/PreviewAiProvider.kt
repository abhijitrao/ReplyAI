package com.replyai.android.data.ai

import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply

/**
 * Temporary provider used by the first UI slice.
 * Real online/offline providers will implement the same domain contract.
 */
class PreviewAiProvider : AiProvider {

    override suspend fun generateReply(request: CommunicationRequest): GeneratedReply {
        return GeneratedReply(
            reply = "I’ll share an update by tomorrow.",
            translation = "मैं कल तक एक अपडेट साझा कर दूँगा।"
        )
    }
}
