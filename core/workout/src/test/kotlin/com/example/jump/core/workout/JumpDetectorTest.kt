package com.example.jump.core.workout

import org.junit.Assert.assertTrue
import org.junit.Test

class JumpDetectorTest {
  @Test fun rhythmicImpactsAreDetected() {
    val detector = JumpDetector(); var detections = 0
    repeat(500) { sample ->
      val impulse = when (sample % 25) { 0 -> 7f; 1 -> 3.5f; else -> 0f }
      if (detector.onAccelerometer(sample * 20_000_000L, 0f, 0f, 9.81f + impulse) != null) detections++
    }
    assertTrue("Detected $detections", detections in 17..21)
  }

  @Test fun stationaryNoiseDoesNotCreateJumps() {
    val detector = JumpDetector(); var detections = 0
    repeat(1_000) { sample ->
      if (detector.onAccelerometer(sample * 20_000_000L, ((sample % 7) - 3) * .02f, 0f, 9.81f) != null) detections++
    }
    assertTrue(detections == 0)
  }
}
