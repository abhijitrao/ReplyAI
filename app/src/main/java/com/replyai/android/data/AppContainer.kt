package com.replyai.android.data

import android.content.Context
import com.replyai.android.data.ai.AiProviderResolver
import com.replyai.android.data.history.ReplyAiDatabase
import com.replyai.android.data.history.RoomReplyHistoryRepository
import com.replyai.android.data.security.SecureSecretStore
import com.replyai.android.data.settings.SettingsRepository
import com.replyai.android.domain.repository.ReplyHistoryRepository
import com.replyai.android.domain.usecase.DeleteReplyHistoryUseCase
import com.replyai.android.domain.usecase.ObserveReplyHistoryUseCase
import com.replyai.android.domain.usecase.SaveReplyHistoryUseCase

class AppContainer(context: Context) {

    private val applicationContext = context.applicationContext

    val settingsRepository = SettingsRepository(applicationContext)
    val secureSecretStore = SecureSecretStore(applicationContext)
    val aiProviderResolver = AiProviderResolver(
        settingsRepository = settingsRepository,
        secureSecretStore = secureSecretStore
    )

    private val replyAiDatabase = ReplyAiDatabase.getInstance(applicationContext)
    private val replyHistoryRepository: ReplyHistoryRepository =
        RoomReplyHistoryRepository(replyAiDatabase.replyHistoryDao())

    val saveReplyHistoryUseCase = SaveReplyHistoryUseCase(replyHistoryRepository)
    val observeReplyHistoryUseCase = ObserveReplyHistoryUseCase(replyHistoryRepository)
    val deleteReplyHistoryUseCase = DeleteReplyHistoryUseCase(replyHistoryRepository)
}
