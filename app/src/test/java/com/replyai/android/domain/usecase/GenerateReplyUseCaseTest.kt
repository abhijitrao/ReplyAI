package com.replyai.android.domain.usecase

import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GenerateReplyUseCaseTest {

    @Test
    fun `generate reply delegates to provider`() = runTest {
        val expected = GeneratedReply(
            reply = "I will share the update tomorrow.",
            translation = "मैं कल अपडेट साझा कर दूँगा।"
        )
        val useCase = GenerateReplyUseCase(FakeAiProvider(expected))

        val result = useCase(
            CommunicationRequest(inputText = "kal update de dunga")
        )

        assertEquals(expected, result)
    }

    @Test
    fun `blank input is rejected before provider call`() = runTest {
        val useCase = GenerateReplyUseCase(FakeAiProvider())

        assertThrows(IllegalArgumentException::class.java) {
            runBlockingTest {
                useCase(CommunicationRequest(inputText = "   "))
            }
        }
    }

    private fun runBlockingTest(block: suspend () -> Unit) {
        kotlinx.coroutines.runBlocking { block() }
    }

    private class FakeAiProvider(
        private val response: GeneratedReply = GeneratedReply("reply", "translation")
    ) : AiProvider {
        override suspend fun generateReply(request: CommunicationRequest): GeneratedReply = response
    }
}
