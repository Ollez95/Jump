package com.example.jump.core.account

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow

@Singleton
internal class DefaultAccountRepository @Inject constructor(
  private val store: AccountStore,
  private val authenticationGateway: AuthenticationGateway,
  private val mergePlanner: GuestDataMergePlanner,
) : AccountRepository {
  override val session: Flow<AccountSession> = flow {
    emit(store.getOrCreateGuest())
    emitAll(store.session.filterNotNull())
  }.distinctUntilChanged()

  override suspend fun currentSession(): AccountSession = store.getOrCreateGuest()

  override suspend fun signIn(request: SignInRequest): SignInResult {
    val guestSession = try {
      store.getOrCreateGuest()
    } catch (error: Exception) {
      return SignInResult.Failure(storageError(error))
    }
    return when (val result = authenticationGateway.signIn(request)) {
      is AuthenticationResult.Failure -> SignInResult.Failure(result.error)
      is AuthenticationResult.Success -> {
        val authenticated = guestSession.copy(mode = AccountMode.Authenticated(result.account))
        try {
          store.save(authenticated)
        } catch (error: Exception) {
          return SignInResult.Failure(storageError(error))
        }
        SignInResult.SignedIn(
          session = authenticated,
          mergePlan = mergePlanner.plan(guestSession, result.account),
        )
      }
    }
  }

  override suspend fun signOut(): SignOutResult {
    val current = try {
      store.getOrCreateGuest()
    } catch (error: Exception) {
      return SignOutResult.Failure(storageError(error))
    }
    val authenticated = (current.mode as? AccountMode.Authenticated)?.account
      ?: return SignOutResult.SignedOut(current)
    val providerResult = authenticationGateway.signOut(authenticated)
    if (providerResult.isFailure) {
      return SignOutResult.Failure(
        AccountOperationError(
          code = AccountErrorCode.UNKNOWN,
          message = providerResult.exceptionOrNull()?.message ?: "Sign-out failed",
          retryable = true,
        ),
      )
    }
    val guest = current.copy(mode = AccountMode.Guest(current.guestAccountId))
    try {
      store.save(guest)
    } catch (error: Exception) {
      return SignOutResult.Failure(storageError(error))
    }
    return SignOutResult.SignedOut(guest)
  }

  private fun storageError(error: Exception) = AccountOperationError(
    code = AccountErrorCode.STORAGE,
    message = error.message ?: "Account storage failed",
    retryable = true,
  )
}
