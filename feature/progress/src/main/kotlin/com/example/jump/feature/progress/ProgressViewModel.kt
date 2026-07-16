package com.example.jump.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.WorkoutProgressAnalyzer
import com.example.jump.core.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProgressViewModel @Inject constructor(
  workouts: WorkoutRepository,
  analyzer: WorkoutProgressAnalyzer,
) : ViewModel() {
  val uiState = workouts.sessions
    .map { ProgressUiState(analyzer.analyze(it)) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}
