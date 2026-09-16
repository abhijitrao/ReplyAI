package com.replyai.android.data

import android.content.Context
import com.replyai.android.data.ai.AiProviderFactory
import com.replyai.android.data.security.SecureSecretStore
import com.replyai.android.data.settings.SettingsRepository

class AppContainer(context: Context) {

    private val applicationContext = context.applicationContext

    val settingsRepository = SettingsRepository(applicationContext)
    val secureSecretStore = SecureSecretStore(applicationContext)
    val aiProviderFactory = AiProviderFactory(secureSecretStore)
}
