package com.example.jump.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jump.MainNavigation
import com.example.jump.core.designsystem.component.JumpBrandMark
import com.example.jump.core.designsystem.component.JumpScreen
import com.example.jump.feature.onboarding.OnboardingRoute

@Composable
fun JumpApp(viewModel: AppViewModel = hiltViewModel()) {
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  when {
    profile == null -> JumpScreen { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { JumpBrandMark(Modifier.size(72.dp)) } }
    profile?.onboardingComplete == false -> OnboardingRoute()
    else -> MainNavigation(viewModel)
  }
}
