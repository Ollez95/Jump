package com.example.jump

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey
@Serializable data object Active : NavKey
@Serializable data object WorkoutSetup : NavKey
@Serializable data object History : NavKey
@Serializable data object Progress : NavKey
@Serializable data object Profile : NavKey
@Serializable data object TrainingProfile : NavKey
@Serializable data class SessionDetail(val sessionId: Long) : NavKey
