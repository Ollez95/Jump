package com.example.jump.core.camera

data class CameraPoseFrame(
  val bodyCenterY: Float,
  val confidence: Float,
  val timestampMillis: Long,
  /** Shoulder-to-hip height as a fraction of the upright image height. */
  val bodyScale: Float = DEFAULT_BODY_SCALE,
)

internal const val DEFAULT_BODY_SCALE = 0.24f
