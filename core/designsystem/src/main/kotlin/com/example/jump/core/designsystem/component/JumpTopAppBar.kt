package com.example.jump.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jump.core.designsystem.preview.JumpComponentPreview
import com.example.jump.core.designsystem.preview.JumpLightDarkPreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JumpTopAppBar(
  title: String,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  backContentDescription: String = "Back",
) {
  TopAppBar(
    title = { Text(title, style = MaterialTheme.typography.titleLarge) },
    navigationIcon = {
      JumpBackButton(onClick = onBack, contentDescription = backContentDescription)
    },
    modifier = modifier,
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background,
      navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
      titleContentColor = MaterialTheme.colorScheme.onBackground,
    ),
  )
}

@JumpLightDarkPreviews
@Composable
private fun JumpTopAppBarPreview() {
  JumpComponentPreview {
    JumpTopAppBar(title = "Session details", onBack = {})
  }
}
