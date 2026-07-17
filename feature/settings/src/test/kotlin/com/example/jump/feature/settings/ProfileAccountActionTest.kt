package com.example.jump.feature.settings

import com.example.jump.core.account.AccountErrorCode
import com.example.jump.core.account.AccountOperationError
import com.example.jump.core.account.AccountSyncResult
import com.example.jump.core.account.BackupResult
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProfileAccountActionTest {
  private val offlineError = AccountOperationError(
    code = AccountErrorCode.OFFLINE_ONLY,
    message = "offline",
    retryable = false,
  )

  @Test
  fun `completed backup reports success`() {
    assertThat(BackupResult.Completed(4).toProfileAccountAction())
      .isEqualTo(ProfileAccountAction.BackupComplete)
  }

  @Test
  fun `offline backup reports unavailable without losing local state`() {
    assertThat(BackupResult.Failure(offlineError).toProfileAccountAction())
      .isEqualTo(ProfileAccountAction.Unavailable)
  }

  @Test
  fun `partial sync remains distinct from failure`() {
    val result = AccountSyncResult.Partial(uploaded = 2, downloaded = 1, failures = listOf(offlineError))

    assertThat(result.toProfileAccountAction()).isEqualTo(ProfileAccountAction.SyncPartial)
  }
}
