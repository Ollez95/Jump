package com.example.jump.core.data.repository

import com.example.jump.core.domain.GamificationTimeProvider
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemGamificationTimeProvider @Inject constructor() : GamificationTimeProvider {
  override fun nowEpochMillis(): Long = System.currentTimeMillis()
  override fun timeZoneId(): String = TimeZone.getDefault().id
}
