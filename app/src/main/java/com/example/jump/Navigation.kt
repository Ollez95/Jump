package com.example.jump

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.jump.core.designsystem.component.JumpNavigationBar
import com.example.jump.core.designsystem.component.JumpNavigationIcon
import com.example.jump.core.designsystem.component.JumpNavigationItem
import com.example.jump.core.model.WorkoutPlan
import com.example.jump.core.model.CountingMode
import com.example.jump.feature.history.HistoryRoute
import com.example.jump.feature.history.SessionDetailRoute
import com.example.jump.feature.home.HomeRoute
import com.example.jump.feature.progress.ProgressRoute
import com.example.jump.feature.settings.SettingsRoute
import com.example.jump.feature.workout.WorkoutRoute
import com.example.jump.feature.workoutsetup.WorkoutSetupRoute
import com.example.jump.ui.AppViewModel

@Composable
fun MainNavigation(viewModel: AppViewModel) {
  val backStack = rememberNavBackStack(Main)
  val current = backStack.lastOrNull()
  val context = LocalContext.current
  var pendingStart by remember { mutableStateOf<Pair<WorkoutPlan, CountingMode>?>(null) }
  var permissionError by remember { mutableStateOf<CountingMode?>(null) }
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
    val pending = pendingStart
    val counterPermissionGranted = when (pending?.second) {
      CountingMode.CAMERA -> result[Manifest.permission.CAMERA] == true || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
      CountingMode.MOTION -> Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || result[Manifest.permission.ACTIVITY_RECOGNITION] == true ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
      null -> false
    }
    if (counterPermissionGranted && pending != null) {
      viewModel.start(pending.first, pending.second)
      if (backStack.lastOrNull() !is Active) backStack.add(Active)
      permissionError = null
    } else permissionError = pending?.second
    pendingStart = null
  }

  fun requestStart(plan: WorkoutPlan, countingMode: CountingMode) {
    pendingStart = plan to countingMode
    val required = buildList {
      if (countingMode == CountingMode.CAMERA && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.CAMERA)
      if (countingMode == CountingMode.MOTION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.ACTIVITY_RECOGNITION)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.POST_NOTIFICATIONS)
    }
    if (required.isEmpty()) { viewModel.start(plan, countingMode); backStack.add(Active); pendingStart = null; permissionError = null } else launcher.launch(required.toTypedArray())
  }

  val showBottomBar = current is Main || current is History || current is Settings
  val tabs: List<Pair<NavKey, JumpNavigationItem>> = listOf(
    Main to JumpNavigationItem("Today", JumpNavigationIcon.TODAY),
    History to JumpNavigationItem("History", JumpNavigationIcon.HISTORY),
    Settings to JumpNavigationItem("Settings", JumpNavigationIcon.SETTINGS),
  )
  Scaffold(bottomBar = {
    if (showBottomBar) {
      JumpNavigationBar(
        items = tabs.map { it.second },
        selectedIndex = tabs.indexOfFirst { it.first::class == current::class },
        onItemSelected = { index -> backStack.clear(); backStack.add(tabs[index].first) },
      )
    }
  }, containerColor = MaterialTheme.colorScheme.background) { padding ->
    NavDisplay(
      modifier = Modifier.padding(padding).consumeWindowInsets(padding),
      backStack = backStack,
      onBack = { backStack.removeLastOrNull() },
      entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()),
      entryProvider = entryProvider {
        entry<Main> {
          HomeRoute(
            permissionError = permissionError,
            onStart = ::requestStart,
            onContinue = { if (backStack.lastOrNull() !is Active) backStack.add(Active) },
            onConfigureWorkout = { backStack.add(WorkoutSetup) },
          )
        }
        entry<WorkoutSetup> {
          WorkoutSetupRoute(
            onBack = { backStack.removeLastOrNull() },
            onStart = ::requestStart,
          )
        }
        entry<Active> { WorkoutRoute(onDone = { backStack.clear(); backStack.add(Main) }) }
        entry<History> {
          HistoryRoute(
            onOpen = { backStack.add(SessionDetail(it)) },
            onProgress = { backStack.add(Progress) },
          )
        }
        entry<Progress> { ProgressRoute(onBack = { backStack.removeLastOrNull() }) }
        entry<Settings> { SettingsRoute() }
        entry<SessionDetail> { key -> SessionDetailRoute(sessionId = key.sessionId, onBack = { backStack.removeLastOrNull() }) }
      },
    )
  }
}
