package com.example.jump.core.camera

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.io.Closeable
import java.util.concurrent.Executor

internal class CameraPoseAnalyzer(
  private val callbackExecutor: Executor,
  private val onFrame: (CameraPoseFrame) -> Unit,
  private val onMissing: (Long) -> Unit,
  private val onError: (Throwable) -> Unit,
) : ImageAnalysis.Analyzer, Closeable {
  private val detector: PoseDetector = PoseDetection.getClient(
    PoseDetectorOptions.Builder()
      .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
      .build(),
  )

  @ExperimentalGetImage
  override fun analyze(imageProxy: ImageProxy) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
      imageProxy.close()
      return
    }
    val rotation = imageProxy.imageInfo.rotationDegrees
    val uprightHeight = if (rotation % 180 == 0) imageProxy.height else imageProxy.width
    val timestampMillis = imageProxy.imageInfo.timestamp / 1_000_000L
    val inputImage = InputImage.fromMediaImage(mediaImage, rotation)
    detector.process(inputImage)
      .addOnSuccessListener(callbackExecutor) { pose ->
        pose.toFrame(uprightHeight, timestampMillis)?.let(onFrame) ?: onMissing(timestampMillis)
      }
      .addOnFailureListener(callbackExecutor, onError)
      .addOnCompleteListener(callbackExecutor) { imageProxy.close() }
  }

  override fun close() = detector.close()
}

private fun Pose.toFrame(imageHeight: Int, timestampMillis: Long): CameraPoseFrame? {
  if (imageHeight <= 0) return null
  val shoulders = listOfNotNull(
    getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
    getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
  ).filter { it.inFrameLikelihood >= MINIMUM_LANDMARK_CONFIDENCE }
  val hips = listOfNotNull(
    getPoseLandmark(PoseLandmark.LEFT_HIP),
    getPoseLandmark(PoseLandmark.RIGHT_HIP),
  ).filter { it.inFrameLikelihood >= MINIMUM_LANDMARK_CONFIDENCE }
  if (shoulders.isEmpty() || hips.isEmpty() || shoulders.size + hips.size < 3) return null

  fun weightedY(landmarks: List<PoseLandmark>): Float {
    val totalWeight = landmarks.sumOf { it.inFrameLikelihood.toDouble() }.toFloat()
    return landmarks.sumOf { (it.position.y * it.inFrameLikelihood).toDouble() }.toFloat() / totalWeight
  }

  val shoulderY = weightedY(shoulders)
  val hipY = weightedY(hips)
  val landmarks = shoulders + hips
  val confidence = landmarks.map { it.inFrameLikelihood }.average().toFloat()
  val centerY = ((shoulderY + hipY) / 2f) / imageHeight
  val bodyScale = kotlin.math.abs(hipY - shoulderY) / imageHeight
  return CameraPoseFrame(
    bodyCenterY = centerY,
    confidence = confidence,
    timestampMillis = timestampMillis,
    bodyScale = bodyScale,
  )
}

private const val MINIMUM_LANDMARK_CONFIDENCE = 0.25f
