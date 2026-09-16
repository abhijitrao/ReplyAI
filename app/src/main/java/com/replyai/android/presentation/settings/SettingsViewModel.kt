package com.replyai.android.presentation.settings

import androidx.lifecycle.ViewModel
import com.replyai.android.data.security.SecureSecretStore
import com.replyai.android.data.settings.SettingsRepository
import com.replyai.android.domain.model.AiMode
import com.replyai.android.domain.model.AiSettings
import com.replyai.android.domain.model.OnlineProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val secureSecretStore: SecureSecretStore
) : ViewModel() {

    val aiSettings: StateFlow<AiSettings> = repository.aiSettings

    private val _isOpenAiApiKeyConfigured = MutableStateFlow(
        secureSecretStore.get(OPENAI_API_KEY).isNullOrBlank().not()
    )
    val isOpenAiApiKeyConfigured: StateFlow<Boolean> =
        _isOpenAiApiKeyConfigured.asStateFlow()

    fun updateMode(mode: AiMode) {
        repository.updateAiSettings(aiSettings.value.copy(mode = mode))
    }

    fun updateOnlineProvider(provider: OnlineProvider) {
        repository.updateAiSettings(aiSettings.value.copy(onlineProvider = provider))
    }

    fun updateOnlineModel(model: String) {
        repository.updateAiSettings(aiSettings.value.copy(onlineModel = model.trim()))
    }

    fun saveOpenAiApiKey(apiKey: String) {
        val normalizedKey = apiKey.trim()
        if (normalizedKey.isBlank()) {
            clearOpenAiApiKey()
            return
        }

        secureSecretStore.put(OPENAI_API_KEY, normalizedKey)
        _isOpenAiApiKeyConfigured.value = true
    }

    fun clearOpenAiApiKey() {
        secureSecretStore.remove(OPENAI_API_KEY)
        _isOpenAiApiKeyConfigured.value = false
    }

    fun getOpenAiApiKey(): String? = secureSecretStore.get(OPENAI_API_KEY)

    private companion object {
        const val OPENAI_API_KEY = "openai_api_key"
    }
}
