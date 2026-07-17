package com.example.jump.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TrainingProfileRoute(
  onBack: () -> Unit,
  onSaved: () -> Unit,
  viewModel: ProfileViewModel = hiltViewModel(),
) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  var level by rememberSaveable(profile.experienceLevel.name) {
    mutableStateOf(profile.experienceLevel)
  }
  var goal by rememberSaveable(profile.trainingGoal.name) {
    mutableStateOf(profile.trainingGoal)
  }
  var frequency by rememberSaveable(profile.sessionsPerWeek) {
    mutableIntStateOf(profile.sessionsPerWeek)
  }
  LaunchedEffect(viewModel, onSaved) {
    viewModel.profileSaved.collect { onSaved() }
  }
  TrainingProfileScreen(
    profile = profile,
    level = level,
    goal = goal,
    frequency = frequency,
    onBack = onBack,
    onLevelSelected = { level = it },
    onGoalSelected = { goal = it },
    onFrequencySelected = { frequency = it },
    onSave = viewModel::updateTrainingProfile,
  )
}
