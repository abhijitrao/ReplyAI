package com.replyai.android.data.ai

import com.replyai.android.data.security.SecureSecretStore
import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.AiMode
import com.replyai.android.domain.model.AiSettings

class AiProviderFactory(
    private val secureSecretStore: SecureSecretStore
) {

    fun create(settings: AiSettings): AiProvider {
        return when (settings.mode) {
            AiMode.ONLINE,
            AiMode.AUTOMATIC -> createOnlineProvider(settings)
            AiMode.OFFLINE -> PreviewAiProvider()
        }
    }

    private fun createOnlineProvider(settings: AiSettings): AiProvider {
        return when (settings.onlineProvider) {
            com.replyai.android.domain.model.OnlineProvider.OPENAI -> {
                OpenAiAiProvider(
                    secureSecretStore = secureSecretStore,
                    model = settings.onlineModel
                )
            }
            com.replyai.android.domain.model.OnlineProvider.GEMINI,
            com.replyai.android.domain.model.OnlineProvider.CUSTOM -> {
                throw UnsupportedOperationException(
                    "The selected online provider is not implemented yet."
                )
            }
        }
    }
}
