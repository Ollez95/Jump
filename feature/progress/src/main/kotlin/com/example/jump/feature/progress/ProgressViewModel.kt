package com.example.jump.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jump.core.domain.WorkoutProgressAnalyzer
import com.example.jump.core.domain.repository.WorkoutRepository
import com.example.jump.core.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProgressViewModel @Inject constructor(
  workouts: WorkoutRepository,
  analyzer: WorkoutProgressAnalyzer,
  gamification: GamificationRepository,
) : ViewModel() {
  val uiState = combine(workouts.sessions, gamification.state) { sessions, rewards ->
    ProgressUiState(report = analyzer.analyze(sessions), rewards = rewards)
  }
    .catch { emit(ProgressUiState(loadFailed = true)) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}
