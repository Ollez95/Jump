package com.example.jump.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jump.R
import com.example.jump.core.account.AccountErrorCode
import com.example.jump.core.designsystem.component.branding.JumpBrandMark
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.button.JumpSecondaryButton
import com.example.jump.core.designsystem.component.feedback.JumpInfoBanner
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews
import com.example.jump.core.designsystem.theme.JumpTheme

@Composable
fun WelcomeScreen(
  state: WelcomeUiState,
  onContinueAsGuest: () -> Unit,
  onGoogle: () -> Unit,
  onEmail: () -> Unit,
) {
  JumpScreen {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 36.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          JumpBrandMark(Modifier.size(88.dp))
          Text(
            text = stringResource(R.string.welcome_brand),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = stringResource(R.string.welcome_title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
          )
          Text(
            text = stringResource(R.string.welcome_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )
          Spacer(Modifier.height(8.dp))
          JumpPrimaryButton(
            label = stringResource(R.string.welcome_guest_action),
            onClick = onContinueAsGuest,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.actionInProgress,
          )
          JumpSecondaryButton(
            label = stringResource(R.string.welcome_google_action),
            onClick = onGoogle,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.actionInProgress,
          )
          JumpSecondaryButton(
            label = stringResource(R.string.welcome_email_action),
            onClick = onEmail,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.actionInProgress,
          )
          Text(
            text = stringResource(R.string.welcome_offline_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )
          state.accountError?.let { error ->
            JumpInfoBanner(
              title = stringResource(R.string.welcome_account_unavailable_title),
              message = stringResource(accountErrorMessage(error)),
              isError = true,
            )
          }
          Text(
            text = stringResource(R.string.welcome_privacy),
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

private fun accountErrorMessage(error: AccountErrorCode): Int = when (error) {
  AccountErrorCode.OFFLINE_ONLY, AccountErrorCode.PROVIDER_UNAVAILABLE -> R.string.welcome_account_offline_error
  AccountErrorCode.REJECTED -> R.string.welcome_account_rejected_error
  else -> R.string.welcome_account_generic_error
}

@JumpLightDarkPreviews
@Composable
private fun WelcomeScreenPreview() {
  JumpTheme {
    WelcomeScreen(
      state = WelcomeUiState(),
      onContinueAsGuest = {},
      onGoogle = {},
      onEmail = {},
    )
  }
}
