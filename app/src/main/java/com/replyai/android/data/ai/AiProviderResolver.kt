package com.replyai.android.data.ai

import com.replyai.android.data.security.SecureSecretStore
import com.replyai.android.data.settings.SettingsRepository
import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.AiMode
import com.replyai.android.domain.model.AiSettings
import com.replyai.android.domain.model.OnlineProvider

class AiProviderResolver(
    private val settingsRepository: SettingsRepository,
    private val secureSecretStore: SecureSecretStore
) {

    fun resolve(settings: AiSettings = settingsRepository.aiSettings.value): AiProvider {
        return when (settings.mode) {
            AiMode.ONLINE -> onlineProvider(settings)
            AiMode.OFFLINE -> UnavailableAiProvider(
                "Offline AI model is not configured yet."
            )
            AiMode.AUTOMATIC -> automaticProvider(settings)
        }
    }

    private fun automaticProvider(settings: AiSettings): AiProvider {
        return if (settings.onlineProviderSupportsConfiguration()) {
            onlineProvider(settings)
        } else {
            UnavailableAiProvider(
                "Configure an online AI provider and API key in Settings."
            )
        }
    }

    private fun onlineProvider(settings: AiSettings): AiProvider {
        return when (settings.onlineProvider) {
            OnlineProvider.OPENAI -> {
                if (!settings.onlineProviderSupportsConfiguration()) {
                    return UnavailableAiProvider(
                        "Configure your OpenAI API key in Settings."
                    )
                }

                OpenAiAiProvider(
                    secureSecretStore = secureSecretStore,
                    model = settings.onlineModel
                )
            }

            OnlineProvider.GEMINI,
            OnlineProvider.CUSTOM -> UnavailableAiProvider(
                "The selected online provider is not implemented yet."
            )
        }
    }

    private fun AiSettings.onlineProviderSupportsConfiguration(): Boolean {
        return onlineProvider == OnlineProvider.OPENAI &&
            secureSecretStore.get(OPENAI_API_KEY).isNullOrBlank().not()
    }

    private companion object {
        const val OPENAI_API_KEY = "openai_api_key"
    }
}
