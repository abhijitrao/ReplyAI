package com.replyai.android.presentation.settings

import androidx.lifecycle.ViewModel
import com.replyai.android.data.settings.SettingsRepository
import com.replyai.android.domain.model.AiMode
import com.replyai.android.domain.model.AiSettings
import com.replyai.android.domain.model.OnlineProvider
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val aiSettings: StateFlow<AiSettings> = repository.aiSettings

    fun updateMode(mode: AiMode) {
        repository.updateAiSettings(aiSettings.value.copy(mode = mode))
    }

    fun updateOnlineProvider(provider: OnlineProvider) {
        repository.updateAiSettings(aiSettings.value.copy(onlineProvider = provider))
    }

    fun updateOnlineModel(model: String) {
        repository.updateAiSettings(aiSettings.value.copy(onlineModel = model))
    }
}
