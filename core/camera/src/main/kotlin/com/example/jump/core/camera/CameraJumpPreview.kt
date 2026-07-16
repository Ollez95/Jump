package com.example.jump.core.camera

import android.annotation.SuppressLint
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
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
