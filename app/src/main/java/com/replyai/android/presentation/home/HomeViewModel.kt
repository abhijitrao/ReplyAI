package com.replyai.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply
import com.replyai.android.domain.model.ReplyHistoryItem
import com.replyai.android.domain.model.ResponseLength
import com.replyai.android.domain.model.Tone
import com.replyai.android.domain.usecase.GenerateReplyUseCase
import com.replyai.android.domain.usecase.SaveReplyHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

data class HomeUiState(
    val inputText: String = "",
    val tone: Tone = Tone.PROFESSIONAL,
    val responseLength: ResponseLength = ResponseLength.SHORT,
    val isGenerating: Boolean = false,
    val generatedReply: GeneratedReply? = null,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val generateReply: GenerateReplyUseCase,
    private val saveReplyHistory: SaveReplyHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateInput(value: String) {
        _uiState.value = _uiState.value.copy(
            inputText = value,
            errorMessage = null
        )
    }

    fun updateTone(tone: Tone) {
        _uiState.value = _uiState.value.copy(tone = tone)
    }

    fun updateResponseLength(length: ResponseLength) {
        _uiState.value = _uiState.value.copy(responseLength = length)
    }

    fun generate() {
        val state = _uiState.value
        if (state.inputText.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter what you want to say.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                errorMessage = null
            )

            runCatching {
                generateReply(
                    CommunicationRequest(
                        inputText = state.inputText,
                        tone = state.tone,
                        responseLength = state.responseLength
                    )
                )
            }.onSuccess { result ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    generatedReply = result
                )

                runCatching {
                    saveReplyHistory(
                        ReplyHistoryItem(
                            id = 0,
                            inputText = state.inputText.trim(),
                            reply = result.reply,
                            translation = result.translation,
                            tone = state.tone,
                            responseLength = state.responseLength,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = error.toUserMessage()
                )
            }
        }
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is IllegalStateException -> message ?: "AI configuration is incomplete."
        is UnsupportedOperationException -> message ?: "The selected AI provider is unavailable."
        is IOException -> message?.takeIf { it.isNotBlank() }
            ?: "The AI service could not be reached. Please check your connection and AI settings."
        else -> "Unable to generate a reply. Please check your connection and AI settings."
    }
}
