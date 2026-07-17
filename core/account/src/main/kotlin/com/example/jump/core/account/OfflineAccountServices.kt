package com.example.jump.core.account

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal val offlineOnlyError = AccountOperationError(
  code = AccountErrorCode.OFFLINE_ONLY,
  message = "Cloud account services are unavailable in offline-only mode.",
  retryable = false,
)

@Singleton
internal class OfflineAuthenticationGateway @Inject constructor() : AuthenticationGateway {
  override suspend fun signIn(request: SignInRequest): AuthenticationResult =
    AuthenticationResult.Failure(offlineOnlyError)

  override suspend fun signOut(account: AuthenticatedAccount): Result<Unit> = Result.success(Unit)
}

@Singleton
internal class OfflineAccountBackupService @Inject constructor() : AccountBackupService {
  override val status: Flow<BackupStatus> = flowOf(BackupStatus.Disabled)
  override suspend fun backup(): BackupResult = BackupResult.Failure(offlineOnlyError)
}

@Singleton
internal class OfflineAccountSyncService @Inject constructor() : AccountSyncService {
  override val status: Flow<AccountSyncStatus> = flowOf(AccountSyncStatus.Disabled)
  override suspend fun sync(reason: SyncReason): AccountSyncResult =
    AccountSyncResult.Failure(offlineOnlyError)
}
