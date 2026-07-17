package com.example.jump.core.account

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class OfflineAccountServicesTest {
  @Test
  fun offlineAuthenticationReturnsExplicitNonRetryableError() = runTest {
    val result = OfflineAuthenticationGateway().signIn(
      SignInRequest(AccountProviderId("google")),
    ) as AuthenticationResult.Failure

    assertEquals(AccountErrorCode.OFFLINE_ONLY, result.error.code)
    assertFalse(result.error.retryable)
  }

  @Test
  fun offlineBackupIsDisabledAndFailsExplicitly() = runTest {
    val service = OfflineAccountBackupService()

    assertEquals(BackupStatus.Disabled, service.status.first())
    val result = service.backup() as BackupResult.Failure
    assertEquals(AccountErrorCode.OFFLINE_ONLY, result.error.code)
  }

  @Test
  fun offlineSyncIsDisabledAndDoesNotSilentlySucceed() = runTest {
    val service = OfflineAccountSyncService()

    assertEquals(AccountSyncStatus.Disabled, service.status.first())
    val result = service.sync() as AccountSyncResult.Failure
    assertEquals(AccountErrorCode.OFFLINE_ONLY, result.error.code)
  }
}
