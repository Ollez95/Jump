package com.example.jump.core.camera

data class CameraPoseFrame(
  val bodyCenterY: Float,
  val confidence: Float,
  val timestampMillis: Long,
  /** Shoulder-to-hip height as a fraction of the upright image height. */
  val bodyScale: Float = DEFAULT_BODY_SCALE,
)

private const val DEFAULT_BODY_SCALE = 0.24f

enum class CameraTrackingState {
  CALIBRATING,
  TRACKING,
  PERSON_NOT_VISIBLE,
  CAMERA_ERROR,
}

data class CameraJumpResult(
  val jumpDetected: Boolean,
  val trackingState: CameraTrackingState,
)

/**
 * Detects a jump from a vertical body-position cycle. Movement is normalized by the observed
 * torso height, keeping sensitivity stable across camera resolutions and viewing distances.
 */
class CameraJumpDetector(
  private val calibrationFrameCount: Int = 18,
  private val minimumConfidence: Float = 0.40f,
  /** Vertical movement measured in shoulder-to-hip lengths instead of image pixels. */
  private val takeoffThreshold: Float = 0.075f,
  private val landingThreshold: Float = 0.04f,
  private val minimumReturnFromPeak: Float = 0.045f,
  private val returnFromPeakRatio: Float = 0.35f,
  private val riseAfterLandingThreshold: Float = 0.025f,
  private val minimumAirTimeMillis: Long = 90,
  private val maximumAirTimeMillis: Long = 1_200,
  private val minimumJumpIntervalMillis: Long = 160,
) {
  private val calibrationPositions = ArrayList<Float>(calibrationFrameCount)
  private val calibrationScales = ArrayList<Float>(calibrationFrameCount)
  private var smoothedPosition: Float? = null
  private var baselinePosition = 0f
  private var baselineBodyScale = DEFAULT_BODY_SCALE
  private var airborneSinceMillis: Long? = null
  private var peakDisplacement = 0f
  private var landingPosition: Float? = null
  private var recoveringAfterJump = false
  private var lastJumpMillis = Long.MIN_VALUE
  private var lastVisibleMillis = 0L

  fun onFrame(frame: CameraPoseFrame): CameraJumpResult {
    if (
      frame.confidence < minimumConfidence ||
      frame.bodyCenterY !in 0f..1f ||
      frame.bodyScale !in MINIMUM_BODY_SCALE..MAXIMUM_BODY_SCALE
    ) {
      return onMissingFrame(frame.timestampMillis)
    }
    lastVisibleMillis = frame.timestampMillis
    val smoothed = smoothedPosition?.let { previous -> previous * 0.32f + frame.bodyCenterY * 0.68f } ?: frame.bodyCenterY
    smoothedPosition = smoothed

    if (calibrationPositions.size < calibrationFrameCount) {
      calibrationPositions += smoothed
      calibrationScales += frame.bodyScale
      if (calibrationPositions.size == calibrationFrameCount) {
        baselinePosition = calibrationPositions.average().toFloat()
        baselineBodyScale = calibrationScales.average().toFloat()
      }
      return CameraJumpResult(false, CameraTrackingState.CALIBRATING)
    }

    // A jump becomes smaller in image coordinates as the person moves away from the phone.
    // Dividing by torso height makes the signal approximately invariant to that distance.
    val upwardDisplacement = (baselinePosition - smoothed) / baselineBodyScale
    if (recoveringAfterJump) {
      val lowestPosition = maxOf(landingPosition ?: smoothed, smoothed)
      landingPosition = lowestPosition
      val risingAgain = (lowestPosition - smoothed) / baselineBodyScale >= riseAfterLandingThreshold
      if (risingAgain) {
        baselinePosition = lowestPosition
        baselineBodyScale = baselineBodyScale * 0.9f + frame.bodyScale * 0.1f
        recoveringAfterJump = false
        landingPosition = null
      }
      return CameraJumpResult(false, CameraTrackingState.TRACKING)
    }

    val airborneSince = airborneSinceMillis
    if (airborneSince == null) {
      if (upwardDisplacement > takeoffThreshold) {
        airborneSinceMillis = frame.timestampMillis
        peakDisplacement = upwardDisplacement
        landingPosition = null
      } else if (kotlin.math.abs(upwardDisplacement) < landingThreshold) {
        baselinePosition = baselinePosition * 0.97f + smoothed * 0.03f
        baselineBodyScale = baselineBodyScale * 0.97f + frame.bodyScale * 0.03f
      }
      return CameraJumpResult(false, CameraTrackingState.TRACKING)
    }

    val airTime = frame.timestampMillis - airborneSince
    if (airTime > maximumAirTimeMillis) {
      resetJumpCycle()
      return CameraJumpResult(false, CameraTrackingState.TRACKING)
    }

    peakDisplacement = maxOf(peakDisplacement, upwardDisplacement)
    val returnedFromPeak = peakDisplacement - upwardDisplacement
    val descendingEnough = returnedFromPeak >= maxOf(
      minimumReturnFromPeak,
      peakDisplacement * returnFromPeakRatio,
    )
    if (descendingEnough) {
      landingPosition = maxOf(landingPosition ?: smoothed, smoothed)
    }

    if (landingPosition != null && airTime >= minimumAirTimeMillis) {
      val interval = if (lastJumpMillis == Long.MIN_VALUE) Long.MAX_VALUE else frame.timestampMillis - lastJumpMillis
      resetJumpCycle()
      recoveringAfterJump = true
      landingPosition = smoothed
      if (interval >= minimumJumpIntervalMillis) {
        lastJumpMillis = frame.timestampMillis
        return CameraJumpResult(true, CameraTrackingState.TRACKING)
      }
    }
    return CameraJumpResult(false, CameraTrackingState.TRACKING)
  }

  fun onMissingFrame(timestampMillis: Long): CameraJumpResult {
    if (lastVisibleMillis > 0 && timestampMillis - lastVisibleMillis > 1_000) reset()
    return CameraJumpResult(false, CameraTrackingState.PERSON_NOT_VISIBLE)
  }

  fun reset() {
    calibrationPositions.clear()
    calibrationScales.clear()
    smoothedPosition = null
    baselinePosition = 0f
    baselineBodyScale = DEFAULT_BODY_SCALE
    resetJumpCycle()
    recoveringAfterJump = false
    lastJumpMillis = Long.MIN_VALUE
    lastVisibleMillis = 0L
  }

  private fun resetJumpCycle() {
    airborneSinceMillis = null
    peakDisplacement = 0f
    landingPosition = null
  }

  private companion object {
    const val MINIMUM_BODY_SCALE = 0.035f
    const val MAXIMUM_BODY_SCALE = 0.60f
  }
}
