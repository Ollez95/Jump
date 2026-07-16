package com.example.jump.core.camera

import android.annotation.SuppressLint
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.io.Closeable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("MissingPermission")
@Composable
fun CameraJumpPreview(
  onJump: () -> Unit,
  onTrackingState: (CameraTrackingState) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val targetRotation = LocalView.current.display?.rotation ?: Surface.ROTATION_0
  val currentOnJump = rememberUpdatedState(onJump)
  val currentOnTrackingState = rememberUpdatedState(onTrackingState)
  val previewView = remember(context) {
    PreviewView(context).apply {
      implementationMode = PreviewView.ImplementationMode.COMPATIBLE
      scaleType = PreviewView.ScaleType.FIT_CENTER
    }
  }

  DisposableEffect(lifecycleOwner, targetRotation) {
    val disposed = AtomicBoolean(false)
    val analysisExecutor = Executors.newSingleThreadExecutor()
    val callbackExecutor = ContextCompat.getMainExecutor(context)
    val jumpDetector = CameraJumpDetector()
    val analyzer = CameraPoseAnalyzer(
      callbackExecutor = callbackExecutor,
      onFrame = { frame ->
        if (!disposed.get()) {
          val result = jumpDetector.onFrame(frame)
          currentOnTrackingState.value(result.trackingState)
          if (result.jumpDetected) currentOnJump.value()
        }
      },
      onMissing = { timestamp ->
        if (!disposed.get()) currentOnTrackingState.value(jumpDetector.onMissingFrame(timestamp).trackingState)
      },
      onError = { if (!disposed.get()) currentOnTrackingState.value(CameraTrackingState.CAMERA_ERROR) },
    )
    var cameraProvider: ProcessCameraProvider? = null
    var preview: Preview? = null
    var imageAnalysis: ImageAnalysis? = null
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
      if (disposed.get()) return@addListener
      runCatching {
        val provider = cameraProviderFuture.get()
        val resolutionSelector = ResolutionSelector.Builder()
          .setResolutionStrategy(
            ResolutionStrategy(Size(640, 480), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER),
          )
          .build()
        val newPreview = Preview.Builder()
          .setTargetRotation(targetRotation)
          .setResolutionSelector(resolutionSelector)
          .build()
          .also { it.surfaceProvider = previewView.surfaceProvider }
        val newAnalysis = ImageAnalysis.Builder()
          .setTargetRotation(targetRotation)
          .setResolutionSelector(resolutionSelector)
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          .build()
          .also { it.setAnalyzer(analysisExecutor, analyzer) }
        check(provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) { "No front-facing camera is available" }
        provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, newPreview, newAnalysis)
        cameraProvider = provider
        preview = newPreview
        imageAnalysis = newAnalysis
      }.onFailure { currentOnTrackingState.value(CameraTrackingState.CAMERA_ERROR) }
    }, callbackExecutor)

    onDispose {
      disposed.set(true)
      imageAnalysis?.clearAnalyzer()
      val boundPreview = preview
      val boundAnalysis = imageAnalysis
      if (boundPreview != null && boundAnalysis != null) cameraProvider?.unbind(boundPreview, boundAnalysis)
      analyzer.close()
      analysisExecutor.shutdown()
    }
  }

  AndroidView(factory = { previewView }, modifier = modifier)
}

private class CameraPoseAnalyzer(
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
