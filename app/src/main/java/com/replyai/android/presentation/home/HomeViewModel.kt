package com.replyai.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.replyai.android.domain.model.CommunicationRequest
import com.replyai.android.domain.model.GeneratedReply
import com.replyai.android.domain.model.ResponseLength
import com.replyai.android.domain.model.Tone
import com.replyai.android.domain.usecase.GenerateReplyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val inputText: String = "",
    val tone: Tone = Tone.PROFESSIONAL,
    val responseLength: ResponseLength = ResponseLength.SHORT,
    val isGenerating: Boolean = false,
    val generatedReply: GeneratedReply? = null,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val generateReply: GenerateReplyUseCase
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
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = error.message ?: "Unable to generate a reply."
                )
            }
        }
    }
}
