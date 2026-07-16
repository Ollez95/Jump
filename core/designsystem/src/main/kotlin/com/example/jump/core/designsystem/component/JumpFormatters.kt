package com.example.jump.core.designsystem.component

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDuration(millis: Long): String {
  val total = (millis / 1_000).coerceAtLeast(0)
  return "%d:%02d".format(total / 60, total % 60)
}

fun formatDate(epochMillis: Long): String =
  SimpleDateFormat("EEE, d MMM • HH:mm", Locale.getDefault()).format(Date(epochMillis))
