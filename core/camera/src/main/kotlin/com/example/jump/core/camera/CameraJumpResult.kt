package com.example.jump.core.camera

data class CameraJumpResult(
  val jumpDetected: Boolean,
  val trackingState: CameraTrackingState,
)
