package com.example.jump.core.camera

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraJumpDetectorTest {
  @Test fun completeTakeoffAndLandingCountsOneJump() {
    val detector = CameraJumpDetector(calibrationFrameCount = 6)
    var now = 0L
    repeat(6) { detector.onFrame(frame(0.62f, now.also { now += 40 })) }

    val positions = listOf(0.60f, 0.575f, 0.565f, 0.57f, 0.595f, 0.615f, 0.622f)
    val detections = positions.count { position ->
      detector.onFrame(frame(position, now.also { now += 40 })).jumpDetected
    }

    assertEquals(1, detections)
  }

  @Test fun normalTrackingNoiseDoesNotCount() {
    val detector = CameraJumpDetector(calibrationFrameCount = 6)
    var now = 0L
    repeat(6) { detector.onFrame(frame(0.62f, now.also { now += 40 })) }
    val noise = listOf(0.617f, 0.624f, 0.613f, 0.626f, 0.619f, 0.621f, 0.616f)

    assertFalse(noise.any { detector.onFrame(frame(it, now.also { now += 40 })).jumpDetected })
  }

  @Test fun lowConfidenceFramesAreRejectedAndForceRecalibrationAfterGap() {
    val detector = CameraJumpDetector(calibrationFrameCount = 3)
    detector.onFrame(frame(0.62f, 0))
    detector.onFrame(frame(0.62f, 40))
    detector.onFrame(frame(0.62f, 80))

    val missing = detector.onFrame(CameraPoseFrame(0.4f, 0.2f, 1_200))
    val afterGap = detector.onFrame(frame(0.62f, 1_240))

    assertEquals(CameraTrackingState.PERSON_NOT_VISIBLE, missing.trackingState)
    assertEquals(CameraTrackingState.CALIBRATING, afterGap.trackingState)
    assertFalse(afterGap.jumpDetected)
  }

  @Test fun movementMustReturnToBaselineBeforeItCounts() {
    val detector = CameraJumpDetector(calibrationFrameCount = 4, maximumAirTimeMillis = 300)
    var now = 0L
    repeat(4) { detector.onFrame(frame(0.62f, now.also { now += 50 })) }

    detector.onFrame(frame(0.56f, now.also { now += 50 }))
    repeat(8) { detector.onFrame(frame(0.55f, now.also { now += 50 })) }

    assertTrue(detector.onFrame(frame(0.62f, now)).let { !it.jumpDetected && it.trackingState == CameraTrackingState.TRACKING })
  }

  @Test fun sameJumpCountsWhenPersonIsFartherFromCamera() {
    val nearDetections = runScaledJump(bodyCenterY = 0.62f, bodyScale = 0.24f)
    val farDetections = runScaledJump(bodyCenterY = 0.48f, bodyScale = 0.075f)

    assertEquals(1, nearDetections)
    assertEquals(1, farDetections)
  }

  @Test fun continuousSkippingCountsEachMovementCycleWithoutExactBaselineReturn() {
    val detector = CameraJumpDetector(calibrationFrameCount = 6)
    val bodyScale = 0.12f
    val baseline = 0.56f
    var now = 0L
    repeat(6) { detector.onFrame(frame(baseline, now.also { now += 40 }, bodyScale)) }
    val displacements = listOf(
      0.06f, 0.13f, 0.20f, 0.16f, 0.10f, 0.065f,
      0.10f, 0.16f, 0.21f, 0.16f, 0.10f, 0.06f,
      0.10f, 0.16f, 0.20f, 0.14f, 0.07f, 0.02f,
    )

    val detections = displacements.count { displacement ->
      val y = baseline - displacement * bodyScale
      detector.onFrame(frame(y, now.also { now += 40 }, bodyScale)).jumpDetected
    }

    assertEquals(3, detections)
  }

  @Test fun invalidTinyBodyScaleIsRejectedInsteadOfAmplifyingPoseNoise() {
    val detector = CameraJumpDetector(calibrationFrameCount = 3)

    val result = detector.onFrame(frame(y = 0.5f, time = 0L, bodyScale = 0.02f))

    assertEquals(CameraTrackingState.PERSON_NOT_VISIBLE, result.trackingState)
  }

  private fun runScaledJump(bodyCenterY: Float, bodyScale: Float): Int {
    val detector = CameraJumpDetector(calibrationFrameCount = 6)
    var now = 0L
    repeat(6) { detector.onFrame(frame(bodyCenterY, now.also { now += 40 }, bodyScale)) }
    val displacements = listOf(0.04f, 0.11f, 0.20f, 0.16f, 0.09f, 0.03f, 0f)
    return displacements.count { displacement ->
      detector.onFrame(
        frame(bodyCenterY - displacement * bodyScale, now.also { now += 40 }, bodyScale),
      ).jumpDetected
    }
  }

  private fun frame(y: Float, time: Long, bodyScale: Float = 0.24f) =
    CameraPoseFrame(y, 0.95f, time, bodyScale)
}
