package com.example.jump.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileRoute(
  onEditTrainingProfile: () -> Unit,
  viewModel: ProfileViewModel = hiltViewModel(),
) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  val cues by viewModel.cues.collectAsStateWithLifecycle()
  val countingMode by viewModel.countingMode.collectAsStateWithLifecycle()
  val accountSession by viewModel.accountSession.collectAsStateWithLifecycle()
  val backupStatus by viewModel.backupStatus.collectAsStateWithLifecycle()
  val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
  val accountAction by viewModel.accountAction.collectAsStateWithLifecycle()
  ProfileScreen(
    profile = profile,
    cues = cues,
    countingMode = countingMode,
    accountSession = accountSession,
    backupStatus = backupStatus,
    syncStatus = syncStatus,
    accountAction = accountAction,
    onEditTrainingProfile = onEditTrainingProfile,
    onCues = viewModel::updateCues,
    onCountingMode = viewModel::updateCountingMode,
    onBackup = viewModel::backupNow,
    onSync = viewModel::syncNow,
    onSignOut = viewModel::signOut,
  )
}
