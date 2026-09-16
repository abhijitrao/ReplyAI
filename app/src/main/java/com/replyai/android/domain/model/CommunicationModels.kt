package com.replyai.android.domain.model

/**
 * The channel changes how a generated message should be phrased.
 */
enum class CommunicationType {
    GENERAL,
    WHATSAPP,
    TEAMS,
    EMAIL
}

enum class Tone {
    PROFESSIONAL,
    POLITE,
    FRIENDLY,
    FORMAL,
    CASUAL,
    FIRM,
    URGENT
}

enum class ResponseLength {
    SHORT,
    MEDIUM,
    DETAILED
}

data class CommunicationRequest(
    val inputText: String,
    val inputLanguage: String = "auto",
    val communicationType: CommunicationType = CommunicationType.GENERAL,
    val replyLanguage: String = "en",
    val translationLanguage: String = "hi",
    val tone: Tone = Tone.PROFESSIONAL,
    val responseLength: ResponseLength = ResponseLength.SHORT,
    val preserveMeaning: Boolean = true
)

data class GeneratedReply(
    val reply: String,
    val translation: String
)
