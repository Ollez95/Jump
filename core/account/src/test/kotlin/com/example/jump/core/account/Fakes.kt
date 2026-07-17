package com.example.jump.core.account

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class InMemoryAccountStore : AccountStore {
  private val state = MutableStateFlow<AccountSession?>(null)
  private var nextId = 0

  override val session: Flow<AccountSession?> = state

  override suspend fun getOrCreateGuest(): AccountSession = state.value ?: AccountSession(
    localUserId = LocalUserId("local-${++nextId}"),
    guestAccountId = AccountId("guest-${++nextId}"),
    mode = AccountMode.Guest(AccountId("guest-$nextId")),
  ).also { state.value = it }

  override suspend fun save(session: AccountSession) {
    state.value = session
  }
}

internal class FailingAccountStore(
  private val delegate: AccountStore = InMemoryAccountStore(),
  var failReads: Boolean = false,
  var failWrites: Boolean = false,
) : AccountStore {
  override val session: Flow<AccountSession?> = delegate.session

  override suspend fun getOrCreateGuest(): AccountSession {
    if (failReads) error("Read failed")
    return delegate.getOrCreateGuest()
  }

  override suspend fun save(session: AccountSession) {
    if (failWrites) error("Write failed")
    delegate.save(session)
  }
}

internal class FakeAuthenticationGateway(
  var signInResult: AuthenticationResult,
  var signOutResult: Result<Unit> = Result.success(Unit),
) : AuthenticationGateway {
  var signedOutAccount: AuthenticatedAccount? = null

  override suspend fun signIn(request: SignInRequest): AuthenticationResult = signInResult

  override suspend fun signOut(account: AuthenticatedAccount): Result<Unit> {
    signedOutAccount = account
    return signOutResult
  }
}

internal fun authenticatedAccount() = AuthenticatedAccount(
  id = AccountId("account-1"),
  providerId = AccountProviderId("test-provider"),
  providerUserId = "provider-user-1",
  email = "jumper@example.com",
)
