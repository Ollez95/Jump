package com.example.jump.core.account

@JvmInline
value class LocalUserId(val value: String) {
  init {
    require(value.isNotBlank()) { "Local user ID cannot be blank" }
  }
}

@JvmInline
value class AccountId(val value: String) {
  init {
    require(value.isNotBlank()) { "Account ID cannot be blank" }
  }
}

@JvmInline
value class AccountProviderId(val value: String) {
  init {
    require(value.isNotBlank()) { "Account provider ID cannot be blank" }
  }
}

data class AuthenticatedAccount(
  val id: AccountId,
  val providerId: AccountProviderId,
  val providerUserId: String,
  val email: String? = null,
  val displayName: String? = null,
) {
  init {
    require(providerUserId.isNotBlank()) { "Provider user ID cannot be blank" }
  }
}

sealed interface AccountMode {
  data class Guest(val id: AccountId) : AccountMode

  data class Authenticated(val account: AuthenticatedAccount) : AccountMode
}

data class AccountSession(
  val localUserId: LocalUserId,
  val guestAccountId: AccountId,
  val mode: AccountMode,
) {
  val isGuest: Boolean get() = mode is AccountMode.Guest
}

data class SignInRequest(
  val providerId: AccountProviderId,
  val emailHint: String? = null,
)

enum class AccountErrorCode {
  OFFLINE_ONLY,
  AUTHENTICATION_REQUIRED,
  PROVIDER_UNAVAILABLE,
  REJECTED,
  STORAGE,
  UNKNOWN,
}

data class AccountOperationError(
  val code: AccountErrorCode,
  val message: String,
  val retryable: Boolean,
)

sealed interface AuthenticationResult {
  data class Success(val account: AuthenticatedAccount) : AuthenticationResult

  data class Failure(val error: AccountOperationError) : AuthenticationResult
}

sealed interface SignInResult {
  data class SignedIn(
    val session: AccountSession,
    val mergePlan: GuestDataMergePlan,
  ) : SignInResult

  data class Failure(val error: AccountOperationError) : SignInResult
}

sealed interface SignOutResult {
  data class SignedOut(val session: AccountSession) : SignOutResult

  data class Failure(val error: AccountOperationError) : SignOutResult
}
