package com.replyai.android.data.ai

import com.replyai.android.data.security.SecureSecretStore
import com.replyai.android.data.settings.SettingsRepository
import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.AiMode
import com.replyai.android.domain.model.AiSettings

class AiProviderResolver(
    private val settingsRepository: SettingsRepository,
    private val secureSecretStore: SecureSecretStore
) {

    fun resolve(settings: AiSettings = settingsRepository.aiSettings.value): AiProvider {
        return when (settings.mode) {
            AiMode.ONLINE -> onlineProvider(settings)
            AiMode.OFFLINE -> PreviewAiProvider()
            AiMode.AUTOMATIC -> automaticProvider(settings)
        }
    }

    private fun automaticProvider(settings: AiSettings): AiProvider {
        return if (settings.onlineProviderSupportsConfiguration()) {
            onlineProvider(settings)
        } else {
            PreviewAiProvider()
        }
    }

    private fun onlineProvider(settings: AiSettings): AiProvider {
        return when (settings.onlineProvider) {
            com.replyai.android.domain.model.OnlineProvider.OPENAI -> OpenAiAiProvider(
                secureSecretStore = secureSecretStore,
                model = settings.onlineModel
            )

            com.replyai.android.domain.model.OnlineProvider.GEMINI,
            com.replyai.android.domain.model.OnlineProvider.CUSTOM -> throw UnsupportedOperationException(
                "The selected online provider is not implemented yet."
            )
        }
    }

    private fun AiSettings.onlineProviderSupportsConfiguration(): Boolean {
        return onlineProvider == com.replyai.android.domain.model.OnlineProvider.OPENAI &&
            secureSecretStore.get(OPENAI_API_KEY).isNullOrBlank().not()
    }

    private companion object {
        const val OPENAI_API_KEY = "openai_api_key"
    }
}
