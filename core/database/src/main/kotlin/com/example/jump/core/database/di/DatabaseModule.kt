package com.example.jump.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.jump.core.database.JumpDatabase
import com.example.jump.core.database.JumpDatabaseMigrations
import com.example.jump.core.database.GamificationDao
import com.example.jump.core.database.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun providesJumpDatabase(@ApplicationContext context: Context): JumpDatabase =
    Room.databaseBuilder(context, JumpDatabase::class.java, "jump.db")
      .addMigrations(JumpDatabaseMigrations.MIGRATION_1_2)
      .build()

  @Provides fun providesWorkoutDao(database: JumpDatabase): WorkoutDao = database.workoutDao()

  @Provides fun providesGamificationDao(database: JumpDatabase): GamificationDao = database.gamificationDao()
}
