package com.example.jump.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JumpDatabaseMigrationTest {
  @get:Rule
  val helper = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    checkNotNull(JumpDatabase::class.java.canonicalName),
    FrameworkSQLiteOpenHelperFactory(),
  )

  @Test @Throws(IOException::class)
  fun migration1To2CreatesGamificationTablesAndPreservesSessions() {
    helper.createDatabase(TEST_DB, 1).apply {
      execSQL(
        """INSERT INTO workout_sessions (id, planId, title, kind, startedAtEpochMillis, durationMillis, activeMillis, detectedJumps, correctedJumps, averagePace, bestPace, longestStreak, status) VALUES (7, 'test', 'Test', 'QUICK', 1, 2, 2, 3, 4, 5, 6, 7, 'COMPLETED')""",
      )
      close()
    }

    val migrated = helper.runMigrationsAndValidate(TEST_DB, 2, true, JumpDatabaseMigrations.MIGRATION_1_2)
    migrated.query("SELECT correctedJumps FROM workout_sessions WHERE id = 7").use { cursor ->
      assertThat(cursor.moveToFirst()).isTrue()
      assertThat(cursor.getInt(0)).isEqualTo(4)
    }
    migrated.query("SELECT COUNT(*) FROM gamification_profile").use { cursor ->
      assertThat(cursor.moveToFirst()).isTrue()
      assertThat(cursor.getInt(0)).isEqualTo(0)
    }
    migrated.close()
  }

  private companion object { const val TEST_DB = "jump-migration-test" }
}
