package com.replyai.android.data.ai

import com.replyai.android.data.security.SecureSecretStore
import com.replyai.android.domain.ai.AiProvider
import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class OpenAiAiProvider(
    private val secureSecretStore: SecureSecretStore,
    private val model: String
) : AiProvider {

    override suspend fun generateReply(request: CommunicationRequest): GeneratedReply {
        val apiKey = secureSecretStore.get(API_KEY_NAME)
            ?: throw IllegalStateException("OpenAI API key is not configured")

        val connection = (URL(RESPONSES_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val payload = buildRequestBody(request)
            connection.outputStream.use { output ->
                output.write(payload.toByteArray(StandardCharsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            }

            if (responseCode !in 200..299) {
                throw IOException("OpenAI request failed ($responseCode): ${extractError(responseBody)}")
            }

            return parseResponse(responseBody)
        } finally {
            connection.disconnect()
        }
    }

    private fun buildRequestBody(request: CommunicationRequest): String {
        val instructions = """
            You are ReplyAI, a communication assistant.
            Generate a reply for the user's message.
            Reply language: ${request.replyLanguage}
            Translation language: ${request.translationLanguage}
            Communication type: ${request.communicationType.name}
            Tone: ${request.tone.name}
            Response length: ${request.responseLength.name}
            Preserve the original meaning: ${request.preserveMeaning}

            Return ONLY a valid JSON object with exactly these string fields:
            {"reply":"...","translation":"..."}
            Do not use markdown fences or additional fields.
        """.trimIndent()

        return JSONObject()
            .put("model", model.ifBlank { DEFAULT_MODEL })
            .put("instructions", instructions)
            .put("input", request.inputText)
            .toString()
    }

    private fun parseResponse(responseBody: String): GeneratedReply {
        val root = JSONObject(responseBody)
        val outputText = root.optString("output_text").takeIf { it.isNotBlank() }
            ?: findText(root.optJSONArray("output"))
            ?: throw IOException("OpenAI response did not contain text output")

        val jsonText = outputText
            .trim()
            .removePrefix("```")
            .removePrefix("json")
            .removeSuffix("```")
            .trim()

        val generated = runCatching { JSONObject(jsonText) }
            .getOrElse { throw IOException("OpenAI returned an invalid reply format", it) }

        val reply = generated.optString("reply").trim()
        val translation = generated.optString("translation").trim()

        if (reply.isBlank()) {
            throw IOException("OpenAI response contained an empty reply")
        }

        return GeneratedReply(
            reply = reply,
            translation = translation
        )
    }

    private fun findText(value: org.json.JSONArray?): String? {
        if (value == null) return null

        for (index in 0 until value.length()) {
            val item = value.optJSONObject(index) ?: continue
            val directText = item.optString("text").takeIf { it.isNotBlank() }
            if (directText != null) return directText

            val contentText = findText(item.optJSONArray("content"))
            if (!contentText.isNullOrBlank()) return contentText
        }

        return null
    }

    private fun extractError(responseBody: String): String {
        return runCatching {
            JSONObject(responseBody)
                .optJSONObject("error")
                ?.optString("message")
                ?.takeIf { it.isNotBlank() }
        }.getOrNull() ?: "Unknown error"
    }

    private companion object {
        const val RESPONSES_URL = "https://api.openai.com/v1/responses"
        const val API_KEY_NAME = "openai_api_key"
        const val DEFAULT_MODEL = "gpt-5.6-luna"
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 60_000
    }
}
