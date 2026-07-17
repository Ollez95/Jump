package com.example.jump.core.account

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
  val session: Flow<AccountSession>

  suspend fun currentSession(): AccountSession

  suspend fun signIn(request: SignInRequest): SignInResult

  suspend fun signOut(): SignOutResult
}

/** Implemented by a future Firebase, OAuth, or first-party authentication adapter. */
interface AuthenticationGateway {
  suspend fun signIn(request: SignInRequest): AuthenticationResult

  suspend fun signOut(account: AuthenticatedAccount): Result<Unit>
}

sealed interface BackupStatus {
  data object Disabled : BackupStatus

  data object Idle : BackupStatus

  data object Running : BackupStatus

  data class Failed(val error: AccountOperationError) : BackupStatus
}

sealed interface BackupResult {
  data class Completed(val itemCount: Int) : BackupResult

  data class Failure(val error: AccountOperationError) : BackupResult
}

interface AccountBackupService {
  val status: Flow<BackupStatus>

  suspend fun backup(): BackupResult
}

enum class SyncReason { USER_REQUEST, APP_START, LOCAL_CHANGE }

sealed interface AccountSyncStatus {
  data object Disabled : AccountSyncStatus

  data object Idle : AccountSyncStatus

  data object Running : AccountSyncStatus

  data class Failed(val error: AccountOperationError) : AccountSyncStatus
}

sealed interface AccountSyncResult {
  data class Completed(val uploaded: Int, val downloaded: Int) : AccountSyncResult

  data class Partial(
    val uploaded: Int,
    val downloaded: Int,
    val failures: List<AccountOperationError>,
  ) : AccountSyncResult

  data class Failure(val error: AccountOperationError) : AccountSyncResult
}

interface AccountSyncService {
  val status: Flow<AccountSyncStatus>

  suspend fun sync(reason: SyncReason = SyncReason.USER_REQUEST): AccountSyncResult
}
