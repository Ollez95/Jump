package com.example.jump.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.jump.core.designsystem.component.button.JumpPrimaryButton
import com.example.jump.core.designsystem.component.card.JumpCard
import com.example.jump.core.designsystem.component.card.JumpChoiceCard
import com.example.jump.core.designsystem.component.input.JumpNumberChip
import com.example.jump.core.designsystem.component.layout.JumpEyebrow
import com.example.jump.core.designsystem.component.layout.JumpHeader
import com.example.jump.core.designsystem.component.layout.JumpScreen
import com.example.jump.core.model.ExperienceLevel
import com.example.jump.core.model.TrainingGoal
import com.example.jump.core.model.UserProfile

@Composable
fun OnboardingRoute(viewModel: OnboardingViewModel = hiltViewModel()) {
  OnboardingScreen(viewModel::finish)
}

@Composable
fun OnboardingScreen(onFinish: (UserProfile) -> Unit) {
  var level by rememberSaveable { mutableStateOf(ExperienceLevel.BEGINNER) }
  var goal by rememberSaveable { mutableStateOf(TrainingGoal.CONSISTENCY) }
  var frequency by rememberSaveable { mutableIntStateOf(3) }
  JumpScreen {
    LazyColumn(
      Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 22.dp, vertical = 32.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        JumpHeader(
          eyebrow = "Welcome to Jump",
          title = "Build momentum,\none jump at a time.",
          description = "A plan that adapts to your rhythm, goals, and progress.",
          brandMark = true,
        )
      }
      item {
        JumpCard {
          JumpEyebrow("Step 1 of 3")
          Text("Your experience", style = MaterialTheme.typography.titleLarge)
          ChoiceSection(ExperienceLevel.entries, level, { it.pretty() }) { level = it }
        }
      }
      item {
        JumpCard {
          JumpEyebrow("Step 2 of 3")
          Text("Your main goal", style = MaterialTheme.typography.titleLarge)
          ChoiceSection(TrainingGoal.entries, goal, { it.pretty() }) { goal = it }
        }
      }
      item {
        JumpCard {
          JumpEyebrow("Step 3 of 3")
          Text("Sessions per week", style = MaterialTheme.typography.titleLarge)
          Text("Choose a pace that feels sustainable.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            (2..6).forEach { count ->
              JumpNumberChip(count, frequency == count, { frequency = count }, Modifier.weight(1f))
            }
          }
        }
      }
      item {
        JumpPrimaryButton(
          label = "Create my first workout",
          onClick = { onFinish(UserProfile(true, level, goal, frequency)) },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}

@Composable
private fun <T> ChoiceSection(choices: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    choices.forEach { choice -> JumpChoiceCard(label(choice), selected == choice, { onSelect(choice) }) }
  }
}

private fun ExperienceLevel.pretty() = name.lowercase().replaceFirstChar { it.uppercase() }
private fun TrainingGoal.pretty() = name.lowercase().replaceFirstChar { it.uppercase() }
