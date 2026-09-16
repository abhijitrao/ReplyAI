package com.replyai.android.domain.model

enum class AiMode {
    AUTOMATIC,
    ONLINE,
    OFFLINE
}

enum class OnlineProvider {
    OPENAI,
    GEMINI,
    CUSTOM
}

data class AiSettings(
    val mode: AiMode = AiMode.AUTOMATIC,
    val onlineProvider: OnlineProvider = OnlineProvider.OPENAI,
    val onlineModel: String = "",
    val offlineModelId: String? = null
)
