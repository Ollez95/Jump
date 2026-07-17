package com.example.jump.core.domain

interface GamificationTimeProvider {
  fun nowEpochMillis(): Long
  fun timeZoneId(): String
}
