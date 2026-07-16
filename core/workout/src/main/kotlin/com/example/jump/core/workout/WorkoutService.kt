package com.example.jump.core.workout

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.example.jump.core.domain.repository.UserPreferencesRepository
import com.example.jump.core.model.CuePreferences
import com.example.jump.core.model.CountingMode
import com.example.jump.core.model.SessionPhase
import com.example.jump.core.model.SessionStatus
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutService : Service(), SensorEventListener, TextToSpeech.OnInitListener {
  @Inject lateinit var coordinator: WorkoutCoordinator
  @Inject lateinit var preferences: UserPreferencesRepository
  @Inject @WorkoutDispatcher lateinit var workoutDispatcher: CoroutineDispatcher
  @Inject @WorkoutApplicationScope lateinit var applicationScope: CoroutineScope
  private val serviceJob = SupervisorJob()
  private val scope by lazy { CoroutineScope(serviceJob + workoutDispatcher) }
  private lateinit var sensorManager: SensorManager
  private val detector = JumpDetector()
  private var ticker: Job? = null
  private var textToSpeech: TextToSpeech? = null
  private var tone: ToneGenerator? = null
  private var cues = CuePreferences()
  private var lastMilestone = 0
  private var intentionallyStopped = false

  override fun onCreate() {
    super.onCreate()
    sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    textToSpeech = TextToSpeech(this, this)
    tone = ToneGenerator(AudioManager.STREAM_MUSIC, 55)
    createChannel()
    scope.launch { preferences.cues.collectLatest { cues = it } }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action ?: ACTION_START) {
      ACTION_START -> startWorkout()
      ACTION_TOGGLE_PAUSE -> {
        coordinator.togglePause()
        transitionCue(
          getString(
            if (coordinator.state.value.phase == SessionPhase.PAUSED) {
              R.string.workout_cue_paused
            } else {
              R.string.workout_cue_go
            },
          ),
        )
        updateNotification()
      }
      ACTION_PAUSE -> { coordinator.pause(); updateNotification() }
      ACTION_STOP -> finishWorkout(intent?.getStringExtra(EXTRA_STATUS)?.let { runCatching { SessionStatus.valueOf(it) }.getOrNull() } ?: SessionStatus.COMPLETED)
    }
    return START_NOT_STICKY
  }

  private fun startWorkout() {
    intentionallyStopped = false; lastMilestone = 0; detector.reset()
    if (coordinator.state.value.countingMode == CountingMode.MOTION) {
      val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
      if (accelerometer == null) coordinator.state.value.plan?.let { coordinator.prepare(it, CountingMode.MOTION, sensorAvailable = false) }
      else {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
      }
    }
    startForeground(NOTIFICATION_ID, buildNotification())
    transitionCue(getString(R.string.workout_cue_get_ready))
    ticker?.cancel()
    ticker = scope.launch {
      while (true) {
        delay(250)
        coordinator.tick()?.let { transition ->
          when (transition.phase) {
            SessionPhase.ACTIVE -> transitionCue(getString(R.string.workout_cue_go))
            SessionPhase.RESTING -> transitionCue(getString(R.string.workout_cue_rest))
            SessionPhase.COMPLETED -> { finishWorkout(SessionStatus.COMPLETED); return@launch }
            else -> Unit
          }
        }
        updateNotification()
      }
    }
  }

  private fun finishWorkout(status: SessionStatus) {
    if (intentionallyStopped) return
    intentionallyStopped = true; ticker?.cancel(); sensorManager.unregisterListener(this)
    scope.launch {
      coordinator.finish(status)
      transitionCue(
        getString(
          if (status == SessionStatus.COMPLETED) {
            R.string.workout_cue_complete
          } else {
            R.string.workout_cue_saved
          },
        ),
      )
      stopForeground(STOP_FOREGROUND_REMOVE); stopSelf()
    }
  }

  override fun onSensorChanged(event: SensorEvent) {
    when (event.sensor.type) {
      Sensor.TYPE_GYROSCOPE -> detector.onGyroscope(event.values[0], event.values[1], event.values[2])
      Sensor.TYPE_ACCELEROMETER -> if (detector.onAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2])?.confidence?.let { it >= .65f } == true) {
        coordinator.registerJump()
        val jumps = coordinator.state.value.detectedJumps
        if (jumps / 100 > lastMilestone) {
          lastMilestone = jumps / 100
          speak(resources.getQuantityString(R.plurals.workout_jump_count, jumps, jumps))
        }
      }
    }
  }
  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

  private fun transitionCue(words: String) {
    speak(words)
    if (cues.tonesEnabled) tone?.startTone(ToneGenerator.TONE_PROP_BEEP, 140)
    if (cues.vibrationEnabled) vibrate()
  }
  private fun speak(words: String) { if (cues.voiceEnabled) textToSpeech?.speak(words, TextToSpeech.QUEUE_ADD, null, words) }
  private fun vibrate() {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    else @Suppress("DEPRECATION") (getSystemService(VIBRATOR_SERVICE) as Vibrator)
    vibrator.vibrate(VibrationEffect.createOneShot(90, VibrationEffect.DEFAULT_AMPLITUDE))
  }

  private fun buildNotification(): android.app.Notification {
    val state = coordinator.state.value
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent()
    val open = PendingIntent.getActivity(this, 0, launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val pause = PendingIntent.getService(this, 1, Intent(this, WorkoutService::class.java).setAction(ACTION_TOGGLE_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val stop = PendingIntent.getService(this, 2, Intent(this, WorkoutService::class.java).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_media_play)
      .setContentTitle(state.plan?.title ?: getString(R.string.workout_notification_default_title))
      .setContentText(
        getString(
          R.string.workout_notification_content,
          state.detectedJumps,
          state.currentPace,
        ),
      )
      .setContentIntent(open).setOnlyAlertOnce(true).setOngoing(true).setCategory(NotificationCompat.CATEGORY_SERVICE)
      .addAction(
        0,
        getString(
          if (state.phase == SessionPhase.PAUSED) {
            R.string.workout_action_resume
          } else {
            R.string.workout_action_pause
          },
        ),
        pause,
      )
      .addAction(0, getString(R.string.workout_action_finish), stop)
      .build()
  }

  private fun updateNotification() { (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIFICATION_ID, buildNotification()) }
  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_ID, getString(R.string.workout_channel_name), NotificationManager.IMPORTANCE_LOW).apply {
        description = getString(R.string.workout_channel_description)
        setSound(null, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build())
      }
      (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }
  }
  override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) textToSpeech?.language = Locale.getDefault() }
  override fun onDestroy() {
    sensorManager.unregisterListener(this); textToSpeech?.shutdown(); tone?.release()
    if (!intentionallyStopped && coordinator.state.value.isRunning) {
      applicationScope.launch { coordinator.finish(SessionStatus.INTERRUPTED) }
    }
    serviceJob.cancel()
    super.onDestroy()
  }
  override fun onBind(intent: Intent?): IBinder? = null

  companion object {
    const val ACTION_START = "com.example.jump.action.START"
    const val ACTION_TOGGLE_PAUSE = "com.example.jump.action.TOGGLE_PAUSE"
    const val ACTION_PAUSE = "com.example.jump.action.PAUSE"
    const val ACTION_STOP = "com.example.jump.action.STOP"
    const val EXTRA_STATUS = "status"
    private const val CHANNEL_ID = "active_workout"
    private const val NOTIFICATION_ID = 41
  }
}
