package com.replyai.android.data.settings

import android.content.Context
import com.replyai.android.domain.model.AiMode
import com.replyai.android.domain.model.AiSettings
import com.replyai.android.domain.model.OnlineProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val _aiSettings = MutableStateFlow(loadAiSettings())
    val aiSettings: StateFlow<AiSettings> = _aiSettings.asStateFlow()

    fun updateAiSettings(settings: AiSettings) {
        preferences.edit()
            .putString(KEY_AI_MODE, settings.mode.name)
            .putString(KEY_ONLINE_PROVIDER, settings.onlineProvider.name)
            .putString(KEY_ONLINE_MODEL, settings.onlineModel)
            .putString(KEY_OFFLINE_MODEL, settings.offlineModelId)
            .apply()

        _aiSettings.value = settings
    }

    private fun loadAiSettings(): AiSettings {
        val mode = preferences.getString(KEY_AI_MODE, null)
            ?.let { runCatching { AiMode.valueOf(it) }.getOrNull() }
            ?: AiMode.AUTOMATIC

        val provider = preferences.getString(KEY_ONLINE_PROVIDER, null)
            ?.let { runCatching { OnlineProvider.valueOf(it) }.getOrNull() }
            ?: OnlineProvider.OPENAI

        return AiSettings(
            mode = mode,
            onlineProvider = provider,
            onlineModel = preferences.getString(
                KEY_ONLINE_MODEL,
                AiSettings.DEFAULT_ONLINE_MODEL
            ).orEmpty().ifBlank { AiSettings.DEFAULT_ONLINE_MODEL },
            offlineModelId = preferences.getString(KEY_OFFLINE_MODEL, null)
        )
    }

    private companion object {
        const val PREFERENCES_NAME = "reply_ai_settings"
        const val KEY_AI_MODE = "ai_mode"
        const val KEY_ONLINE_PROVIDER = "online_provider"
        const val KEY_ONLINE_MODEL = "online_model"
        const val KEY_OFFLINE_MODEL = "offline_model"
    }
}
