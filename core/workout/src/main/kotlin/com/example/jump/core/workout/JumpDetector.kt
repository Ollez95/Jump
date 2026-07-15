package com.example.jump.core.workout

import kotlin.math.sqrt

class JumpDetector(private val minimumJumpIntervalNanos: Long = 220_000_000L) {
  private var gravityX = 0f
  private var gravityY = 0f
  private var gravityZ = 0f
  private var smoothed = 0f
  private var previous = 0f
  private var previousSlope = 0f
  private var noiseMean = 0f
  private var noiseVariance = 0f
  private var lastJumpNanos = Long.MIN_VALUE
  private var sampleCount = 0
  private var gyroMagnitude = 0f

  fun onGyroscope(x: Float, y: Float, z: Float) { gyroMagnitude = sqrt(x * x + y * y + z * z) }

  fun onAccelerometer(timestampNanos: Long, x: Float, y: Float, z: Float): Detection? {
    val gravityAlpha = .90f
    gravityX = gravityAlpha * gravityX + (1 - gravityAlpha) * x
    gravityY = gravityAlpha * gravityY + (1 - gravityAlpha) * y
    gravityZ = gravityAlpha * gravityZ + (1 - gravityAlpha) * z
    val lx = x - gravityX
    val ly = y - gravityY
    val lz = z - gravityZ
    val magnitude = sqrt(lx * lx + ly * ly + lz * lz)
    smoothed = .62f * smoothed + .38f * magnitude
    sampleCount++
    val threshold = maxOf(1.45f, noiseMean + 2.1f * sqrt(noiseVariance.coerceAtLeast(0f)))
    val slope = smoothed - previous
    val isPeak = previousSlope > 0 && slope <= 0 && previous > threshold
    val separated = lastJumpNanos == Long.MIN_VALUE || timestampNanos - lastJumpNanos >= minimumJumpIntervalNanos
    previousSlope = slope
    previous = smoothed
    if (smoothed < threshold * .85f) {
      val delta = smoothed - noiseMean
      noiseMean += .025f * delta
      noiseVariance = .975f * noiseVariance + .025f * delta * delta
    }
    if (sampleCount > 15 && isPeak && separated) {
      lastJumpNanos = timestampNanos
      val signalConfidence = ((previous - threshold) / threshold).coerceIn(0f, 1f)
      return Detection((.65f + signalConfidence * .25f + (gyroMagnitude / 8f).coerceIn(0f, .2f)).coerceAtMost(1f))
    }
    return null
  }

  fun reset() {
    gravityX = 0f; gravityY = 0f; gravityZ = 0f; smoothed = 0f; previous = 0f; previousSlope = 0f
    noiseMean = 0f; noiseVariance = 0f; lastJumpNanos = Long.MIN_VALUE; sampleCount = 0; gyroMagnitude = 0f
  }

  data class Detection(val confidence: Float)
}
