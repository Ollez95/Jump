package com.example.jump.core.account

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class AccountPersistenceTest {
  @get:Rule val temporaryFolder = TemporaryFolder()

  @Test
  fun guestIdentifiersPersistAcrossStoreRecreation() = runTest {
    val file = File(temporaryFolder.root, "account.preferences_pb")
    val firstScope = CoroutineScope(StandardTestDispatcher(testScheduler))
    val firstStore = PreferencesAccountStore(
      PreferenceDataStoreFactory.create(scope = firstScope, produceFile = { file }),
      sequentialIds(),
    )

    val created = firstStore.getOrCreateGuest()
    assertEquals(created, firstStore.session.first())
    firstScope.cancel()

    val secondScope = CoroutineScope(StandardTestDispatcher(testScheduler))
    val secondStore = PreferencesAccountStore(
      PreferenceDataStoreFactory.create(scope = secondScope, produceFile = { file }),
      AccountIdGenerator { error("Persisted identifiers should be reused") },
    )

    val restored = secondStore.getOrCreateGuest()

    assertEquals(created, restored)
    assertTrue(restored.isGuest)
    secondScope.cancel()
  }

  @Test
  fun authenticatedAccountPersistsWithoutChangingLocalIdentity() = runTest {
    val scope = CoroutineScope(StandardTestDispatcher(testScheduler))
    val store = PreferencesAccountStore(
      PreferenceDataStoreFactory.create(
        scope = scope,
        produceFile = { File(temporaryFolder.root, "authenticated.preferences_pb") },
      ),
      sequentialIds(),
    )
    val guest = store.getOrCreateGuest()
    val authenticated = guest.copy(mode = AccountMode.Authenticated(authenticatedAccount()))

    store.save(authenticated)
    val restored = store.session.first()

    assertEquals(authenticated, restored)
    assertEquals(guest.localUserId, restored?.localUserId)
    assertEquals(guest.guestAccountId, restored?.guestAccountId)
    scope.cancel()
  }

  private fun sequentialIds(): AccountIdGenerator {
    var value = 0
    return AccountIdGenerator { "generated-${++value}" }
  }
}
