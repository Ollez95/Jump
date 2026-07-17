package com.example.jump.feature.history

sealed interface HistoryUiState {
  data object Loading : HistoryUiState
  data class Loaded(val sessions: List<HistorySessionUiModel>) : HistoryUiState
  data object Error : HistoryUiState
}
