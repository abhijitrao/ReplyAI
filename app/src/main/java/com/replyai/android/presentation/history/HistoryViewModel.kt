package com.replyai.android.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.replyai.android.domain.model.ReplyHistoryItem
import com.replyai.android.domain.usecase.DeleteReplyHistoryUseCase
import com.replyai.android.domain.usecase.ObserveReplyHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val items: List<ReplyHistoryItem> = emptyList()
)

class HistoryViewModel(
    observeReplyHistory: ObserveReplyHistoryUseCase,
    private val deleteReplyHistory: DeleteReplyHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = observeReplyHistory()
        .map { items -> HistoryUiState(items = items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState()
        )

    fun delete(id: Long) {
        viewModelScope.launch {
            deleteReplyHistory(id)
        }
    }
}
