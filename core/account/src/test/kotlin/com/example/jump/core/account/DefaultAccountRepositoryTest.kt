package com.example.jump.core.account

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAccountRepositoryTest {
  @Test
  fun successfulSignInPreservesLocalIdentityAndProducesMergePlan() = runTest {
    val store = InMemoryAccountStore()
    val guest = store.getOrCreateGuest()
    val account = authenticatedAccount()
    val repository = repository(store, AuthenticationResult.Success(account))

    val result = repository.signIn(SignInRequest(account.providerId))

    assertTrue(result is SignInResult.SignedIn)
    result as SignInResult.SignedIn
    assertEquals(guest.localUserId, result.session.localUserId)
    assertEquals(guest.guestAccountId, result.session.guestAccountId)
    assertEquals(AccountMode.Authenticated(account), result.session.mode)
    assertEquals(account.id, result.mergePlan.destinationAccountId)
  }

  @Test
  fun signOutReturnsToSamePersistedGuestIdentity() = runTest {
    val store = InMemoryAccountStore()
    val account = authenticatedAccount()
    val gateway = FakeAuthenticationGateway(AuthenticationResult.Success(account))
    val repository = DefaultAccountRepository(store, gateway, GuestDataMergePlanner())
    val signedIn = repository.signIn(SignInRequest(account.providerId)) as SignInResult.SignedIn

    val result = repository.signOut()

    assertTrue(result is SignOutResult.SignedOut)
    result as SignOutResult.SignedOut
    assertEquals(signedIn.session.localUserId, result.session.localUserId)
    assertEquals(signedIn.session.guestAccountId, result.session.guestAccountId)
    assertEquals(AccountMode.Guest(signedIn.session.guestAccountId), result.session.mode)
    assertEquals(account, gateway.signedOutAccount)
  }

  @Test
  fun failedSignInLeavesGuestSessionUnchanged() = runTest {
    val store = InMemoryAccountStore()
    val before = store.getOrCreateGuest()
    val error = AccountOperationError(AccountErrorCode.REJECTED, "Rejected", false)
    val repository = repository(store, AuthenticationResult.Failure(error))

    val result = repository.signIn(SignInRequest(AccountProviderId("test")))

    assertEquals(SignInResult.Failure(error), result)
    assertEquals(before, repository.currentSession())
  }

  @Test
  fun providerSignOutFailureKeepsAuthenticatedMode() = runTest {
    val store = InMemoryAccountStore()
    val account = authenticatedAccount()
    val gateway = FakeAuthenticationGateway(
      AuthenticationResult.Success(account),
      Result.failure(IllegalStateException("Provider failed")),
    )
    val repository = DefaultAccountRepository(store, gateway, GuestDataMergePlanner())
    repository.signIn(SignInRequest(account.providerId))

    val result = repository.signOut()

    assertTrue(result is SignOutResult.Failure)
    assertEquals(AccountMode.Authenticated(account), repository.currentSession().mode)
  }

  @Test
  fun storageFailureDuringSignInIsReportedWithoutClaimingSuccess() = runTest {
    val store = FailingAccountStore()
    val account = authenticatedAccount()
    store.getOrCreateGuest()
    store.failWrites = true
    val repository = DefaultAccountRepository(
      store,
      FakeAuthenticationGateway(AuthenticationResult.Success(account)),
      GuestDataMergePlanner(),
    )

    val result = repository.signIn(SignInRequest(account.providerId)) as SignInResult.Failure

    assertEquals(AccountErrorCode.STORAGE, result.error.code)
    assertTrue(result.error.retryable)
  }

  private fun repository(store: AccountStore, authResult: AuthenticationResult) =
    DefaultAccountRepository(store, FakeAuthenticationGateway(authResult), GuestDataMergePlanner())
}
