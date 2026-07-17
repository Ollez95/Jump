package com.example.jump.core.account

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AccountBindingsModule {
  @Binds @Singleton abstract fun bindAccountRepository(implementation: DefaultAccountRepository): AccountRepository

  @Binds @Singleton abstract fun bindAuthenticationGateway(implementation: OfflineAuthenticationGateway): AuthenticationGateway

  @Binds @Singleton abstract fun bindBackupService(implementation: OfflineAccountBackupService): AccountBackupService

  @Binds @Singleton abstract fun bindSyncService(implementation: OfflineAccountSyncService): AccountSyncService

  companion object {
    @Provides
    @Singleton
    fun provideAccountDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
      PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("jump_account.preferences_pb") },
      )

    @Provides
    @Singleton
    internal fun provideAccountStore(dataStore: DataStore<Preferences>): AccountStore =
      PreferencesAccountStore(dataStore, AccountIdGenerator { UUID.randomUUID().toString() })

    @Provides @Singleton fun provideMergePlanner(): GuestDataMergePlanner = GuestDataMergePlanner()
  }
}
