package com.example.jump.core.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class PreferencesAccountStore(
  private val dataStore: DataStore<Preferences>,
  private val idGenerator: AccountIdGenerator,
) : AccountStore {
  override val session: Flow<AccountSession?> = dataStore.data
    .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
    .map(::decode)

  override suspend fun getOrCreateGuest(): AccountSession {
    val values = dataStore.edit { preferences ->
      if (preferences[Keys.localUserId].isNullOrBlank()) {
        preferences[Keys.localUserId] = idGenerator.generate()
      }
      if (preferences[Keys.guestAccountId].isNullOrBlank()) {
        preferences[Keys.guestAccountId] = idGenerator.generate()
      }
      if (preferences[Keys.mode].isNullOrBlank()) preferences[Keys.mode] = MODE_GUEST
    }
    return requireNotNull(decode(values))
  }

  override suspend fun save(session: AccountSession) {
    dataStore.edit { preferences ->
      preferences[Keys.localUserId] = session.localUserId.value
      preferences[Keys.guestAccountId] = session.guestAccountId.value
      when (val mode = session.mode) {
        is AccountMode.Guest -> {
          preferences[Keys.mode] = MODE_GUEST
          clearAuthenticated(preferences)
        }
        is AccountMode.Authenticated -> {
          preferences[Keys.mode] = MODE_AUTHENTICATED
          preferences[Keys.accountId] = mode.account.id.value
          preferences[Keys.providerId] = mode.account.providerId.value
          preferences[Keys.providerUserId] = mode.account.providerUserId
          writeOptional(preferences, Keys.email, mode.account.email)
          writeOptional(preferences, Keys.displayName, mode.account.displayName)
        }
      }
    }
  }

  private fun decode(preferences: Preferences): AccountSession? {
    val localUserId = preferences[Keys.localUserId]?.takeIf(String::isNotBlank) ?: return null
    val guestAccountId = preferences[Keys.guestAccountId]?.takeIf(String::isNotBlank) ?: return null
    val guest = AccountSession(
      localUserId = LocalUserId(localUserId),
      guestAccountId = AccountId(guestAccountId),
      mode = AccountMode.Guest(AccountId(guestAccountId)),
    )
    if (preferences[Keys.mode] != MODE_AUTHENTICATED) return guest

    val accountId = preferences[Keys.accountId]?.takeIf(String::isNotBlank) ?: return guest
    val providerId = preferences[Keys.providerId]?.takeIf(String::isNotBlank) ?: return guest
    val providerUserId = preferences[Keys.providerUserId]?.takeIf(String::isNotBlank) ?: return guest
    return guest.copy(
      mode = AccountMode.Authenticated(
        AuthenticatedAccount(
          id = AccountId(accountId),
          providerId = AccountProviderId(providerId),
          providerUserId = providerUserId,
          email = preferences[Keys.email],
          displayName = preferences[Keys.displayName],
        ),
      ),
    )
  }

  private fun clearAuthenticated(preferences: androidx.datastore.preferences.core.MutablePreferences) {
    preferences.remove(Keys.accountId)
    preferences.remove(Keys.providerId)
    preferences.remove(Keys.providerUserId)
    preferences.remove(Keys.email)
    preferences.remove(Keys.displayName)
  }

  private fun writeOptional(
    preferences: androidx.datastore.preferences.core.MutablePreferences,
    key: Preferences.Key<String>,
    value: String?,
  ) {
    if (value == null) preferences.remove(key) else preferences[key] = value
  }

  private object Keys {
    val localUserId = stringPreferencesKey("account_local_user_id")
    val guestAccountId = stringPreferencesKey("account_guest_id")
    val mode = stringPreferencesKey("account_mode")
    val accountId = stringPreferencesKey("account_authenticated_id")
    val providerId = stringPreferencesKey("account_provider_id")
    val providerUserId = stringPreferencesKey("account_provider_user_id")
    val email = stringPreferencesKey("account_email")
    val displayName = stringPreferencesKey("account_display_name")
  }

  private companion object {
    const val MODE_GUEST = "guest"
    const val MODE_AUTHENTICATED = "authenticated"
  }
}
