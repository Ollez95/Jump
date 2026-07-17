package com.example.jump.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.jump.core.account.AccountMode
import com.example.jump.core.account.AccountSession
import com.example.jump.core.account.AccountSyncStatus
import com.example.jump.core.account.BackupStatus
import com.example.jump.core.account.AccountId
import com.example.jump.core.account.LocalUserId
import com.example.jump.core.designsystem.component.button.JumpSecondaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpChoiceCard
import com.example.jump.core.designsystem.component.card.JumpHeroCard
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.input.JumpSettingRow
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.component.layout.JumpDetailRow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile

@Composable
fun ProfileScreen(
  profile: UserProfile,
  cues: CuePreferences,
  countingMode: CountingMode,
  accountSession: AccountSession?,
  backupStatus: BackupStatus,
  syncStatus: AccountSyncStatus,
  accountAction: ProfileAccountAction?,
  onEditTrainingProfile: () -> Unit,
  onCues: (CuePreferences) -> Unit,
  onCountingMode: (CountingMode) -> Unit,
  onBackup: () -> Unit,
  onSync: () -> Unit,
  onSignOut: () -> Unit,
) {
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = stringResource(R.string.profile_eyebrow),
          title = stringResource(R.string.profile_title),
          description = stringResource(R.string.profile_description),
        )
      }
      accountAction?.let { action ->
        item {
          JumpInfoBanner(
            title = accountActionTitle(action),
            message = accountActionMessage(action),
            isError = action == ProfileAccountAction.Failed || action == ProfileAccountAction.Unavailable,
          )
        }
      }
      item {
        AccountCard(
          accountSession = accountSession,
          actionInProgress = accountAction == ProfileAccountAction.Working,
          onSignOut = onSignOut,
        )
      }
      item {
        BackupAndSyncCard(
          accountSession = accountSession,
          backupStatus = backupStatus,
          syncStatus = syncStatus,
          actionInProgress = accountAction == ProfileAccountAction.Working,
          onBackup = onBackup,
          onSync = onSync,
        )
      }
      item {
        JumpHeroCard(
          eyebrow = stringResource(R.string.training_profile),
          title = experienceLabel(profile.experienceLevel),
          description = stringResource(
            R.string.training_profile_summary,
            goalLabel(profile.trainingGoal),
            pluralStringResource(
              R.plurals.sessions_per_week,
              profile.sessionsPerWeek,
              profile.sessionsPerWeek,
            ),
          ),
          actionLabel = stringResource(R.string.edit_training_profile),
          onAction = onEditTrainingProfile,
          meta = stringResource(R.string.profile_meta),
        )
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.default_counting_method))
          JumpChoiceCard(
            label = stringResource(R.string.counting_pocket),
            description = stringResource(R.string.counting_pocket_description),
            selected = countingMode == CountingMode.MOTION,
            onClick = { onCountingMode(CountingMode.MOTION) },
          )
          JumpChoiceCard(
            label = stringResource(R.string.counting_camera),
            description = stringResource(R.string.counting_camera_description),
            selected = countingMode == CountingMode.CAMERA,
            onClick = { onCountingMode(CountingMode.CAMERA) },
          )
        }
      }
      item {
        JumpCard {
          JumpEyebrow(stringResource(R.string.coaching_cues))
          JumpSettingRow(
            stringResource(R.string.voice_coaching),
            stringResource(R.string.voice_coaching_description),
            cues.voiceEnabled,
            onCheckedChange = { onCues(cues.copy(voiceEnabled = it)) },
          )
          JumpSettingRow(
            stringResource(R.string.transition_tones),
            stringResource(R.string.transition_tones_description),
            cues.tonesEnabled,
            onCheckedChange = { onCues(cues.copy(tonesEnabled = it)) },
          )
          JumpSettingRow(
            stringResource(R.string.vibration),
            stringResource(R.string.vibration_description),
            cues.vibrationEnabled,
            onCheckedChange = { onCues(cues.copy(vibrationEnabled = it)) },
          )
        }
      }
      item {
        JumpInfoBanner(
          title = stringResource(R.string.pocket_counting_title),
          message = stringResource(R.string.pocket_counting_message),
        )
      }
    }
  }
}

@Composable
private fun AccountCard(
  accountSession: AccountSession?,
  actionInProgress: Boolean,
  onSignOut: () -> Unit,
) {
  val mode = accountSession?.mode
  JumpCard {
    JumpEyebrow(stringResource(R.string.account_label))
    when (mode) {
      null -> {
        Text(stringResource(R.string.account_loading), style = MaterialTheme.typography.titleLarge)
        Text(
          stringResource(R.string.account_loading_description),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      is AccountMode.Guest -> {
        Text(stringResource(R.string.account_guest_title), style = MaterialTheme.typography.headlineSmall)
        Text(
          stringResource(R.string.account_guest_description),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        JumpSecondaryButton(
          label = stringResource(R.string.account_provider_unavailable),
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = false,
        )
      }
      is AccountMode.Authenticated -> {
        Text(
          mode.account.displayName ?: mode.account.email ?: stringResource(R.string.account_signed_in),
          style = MaterialTheme.typography.headlineSmall,
        )
        mode.account.email?.let {
          Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        JumpSecondaryButton(
          label = stringResource(R.string.account_sign_out),
          onClick = onSignOut,
          modifier = Modifier.fillMaxWidth(),
          enabled = !actionInProgress,
        )
      }
    }
  }
}

@Composable
private fun BackupAndSyncCard(
  accountSession: AccountSession?,
  backupStatus: BackupStatus,
  syncStatus: AccountSyncStatus,
  actionInProgress: Boolean,
  onBackup: () -> Unit,
  onSync: () -> Unit,
) {
  val authenticated = accountSession?.mode is AccountMode.Authenticated
  val backupEnabled = authenticated && backupStatus !is BackupStatus.Disabled &&
    backupStatus !is BackupStatus.Running && !actionInProgress
  val syncEnabled = authenticated && syncStatus !is AccountSyncStatus.Disabled &&
    syncStatus !is AccountSyncStatus.Running && !actionInProgress
  JumpCard {
    JumpEyebrow(stringResource(R.string.backup_sync_label))
    Text(stringResource(R.string.backup_sync_title), style = MaterialTheme.typography.titleLarge)
    Text(
      if (authenticated) stringResource(R.string.backup_sync_authenticated_description)
      else stringResource(R.string.backup_sync_guest_description),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    JumpDetailRow(stringResource(R.string.backup_status), backupStatusLabel(backupStatus))
    JumpDetailRow(stringResource(R.string.sync_status), syncStatusLabel(syncStatus))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      JumpSecondaryButton(
        label = stringResource(R.string.backup_now),
        onClick = onBackup,
        modifier = Modifier.weight(1f),
        enabled = backupEnabled,
      )
      JumpSecondaryButton(
        label = stringResource(R.string.sync_now),
        onClick = onSync,
        modifier = Modifier.weight(1f),
        enabled = syncEnabled,
      )
    }
  }
}

@Composable
private fun backupStatusLabel(status: BackupStatus): String = stringResource(
  when (status) {
    BackupStatus.Disabled -> R.string.cloud_offline
    BackupStatus.Idle -> R.string.cloud_ready
    BackupStatus.Running -> R.string.cloud_working
    is BackupStatus.Failed -> R.string.cloud_attention
  },
)

@Composable
private fun syncStatusLabel(status: AccountSyncStatus): String = stringResource(
  when (status) {
    AccountSyncStatus.Disabled -> R.string.cloud_offline
    AccountSyncStatus.Idle -> R.string.cloud_ready
    AccountSyncStatus.Running -> R.string.cloud_working
    is AccountSyncStatus.Failed -> R.string.cloud_attention
  },
)

@Composable
private fun accountActionTitle(action: ProfileAccountAction): String = stringResource(
  when (action) {
    ProfileAccountAction.Working -> R.string.account_action_working
    ProfileAccountAction.BackupComplete -> R.string.account_action_backup_complete
    ProfileAccountAction.SyncComplete -> R.string.account_action_sync_complete
    ProfileAccountAction.SyncPartial -> R.string.account_action_sync_partial
    ProfileAccountAction.SignedOut -> R.string.account_action_signed_out
    ProfileAccountAction.Unavailable -> R.string.account_action_unavailable
    ProfileAccountAction.Failed -> R.string.account_action_failed
  },
)

@Composable
private fun accountActionMessage(action: ProfileAccountAction): String = stringResource(
  when (action) {
    ProfileAccountAction.Working -> R.string.account_action_working_message
    ProfileAccountAction.BackupComplete -> R.string.account_action_backup_complete_message
    ProfileAccountAction.SyncComplete -> R.string.account_action_sync_complete_message
    ProfileAccountAction.SyncPartial -> R.string.account_action_sync_partial_message
    ProfileAccountAction.SignedOut -> R.string.account_action_signed_out_message
    ProfileAccountAction.Unavailable -> R.string.account_action_unavailable_message
    ProfileAccountAction.Failed -> R.string.account_action_failed_message
  },
)

@JumpLightDarkPreviews
@Preview(name = "1.5x font", widthDp = 390, heightDp = 884, fontScale = 1.5f, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
  JumpTheme {
    ProfileScreen(
      profile = UserProfile(
        onboardingComplete = true,
        experienceLevel = ExperienceLevel.REGULAR,
        trainingGoal = TrainingGoal.ENDURANCE,
        sessionsPerWeek = 4,
      ),
      cues = CuePreferences(),
      countingMode = CountingMode.MOTION,
      accountSession = AccountSession(
        localUserId = LocalUserId("preview-local"),
        guestAccountId = AccountId("preview-guest"),
        mode = AccountMode.Guest(AccountId("preview-guest")),
      ),
      backupStatus = BackupStatus.Disabled,
      syncStatus = AccountSyncStatus.Disabled,
      accountAction = null,
      onEditTrainingProfile = {},
      onCues = {},
      onCountingMode = {},
      onBackup = {},
      onSync = {},
      onSignOut = {},
    )
  }
}
