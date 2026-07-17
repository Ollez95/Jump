package com.example.jump.core.account

import kotlinx.coroutines.flow.Flow

internal interface AccountStore {
  val session: Flow<AccountSession?>

  suspend fun getOrCreateGuest(): AccountSession

  suspend fun save(session: AccountSession)
}

internal fun interface AccountIdGenerator {
  fun generate(): String
}
