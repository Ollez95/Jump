package com.example.jump

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.jump.core.designsystem.component.navigation.JumpNavigationBar
import com.example.jump.core.designsystem.component.navigation.JumpNavigationIcon
import com.example.jump.core.designsystem.component.navigation.JumpNavigationItem
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
  var permissionError by rememberSaveable { mutableStateOf<CountingMode?>(null) }
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
    if (viewModel.startPendingWorkoutIfPermitted()) {
      if (backStack.lastOrNull() !is Active) backStack.add(Active)
      permissionError = null
    } else {
      permissionError = viewModel.discardPendingWorkoutStart()
    }
  }

  fun requestStart(plan: WorkoutPlan, countingMode: CountingMode) {
    val required = viewModel.prepareWorkoutStart(plan, countingMode)
    if (required.isEmpty()) {
      if (viewModel.startPendingWorkoutIfPermitted()) backStack.add(Active)
      permissionError = null
    } else {
      launcher.launch(required.toTypedArray())
    }
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
