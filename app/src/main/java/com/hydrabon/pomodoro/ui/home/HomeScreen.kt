package com.hydrabon.pomodoro.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hydrabon.pomodoro.model.TimerMode

@Composable
fun HomeScreen(
    state: HomeUiState,
    onStart: (TimerMode) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onQuickSettingsGuide: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.remainingLabel,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModeChip(title = "Focus", selected = state.mode == TimerMode.FOCUS) {
                        onStart(TimerMode.FOCUS)
                    }
                    ModeChip(title = "Short", selected = state.mode == TimerMode.SHORT_BREAK) {
                        onStart(TimerMode.SHORT_BREAK)
                    }
                    ModeChip(title = "Long", selected = state.mode == TimerMode.LONG_BREAK) {
                        onStart(TimerMode.LONG_BREAK)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Today: ${state.completedSessions}/${state.targetSessions} sessions",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Focused ${state.todayFocusMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    state.isRunning -> {
                        Button(onClick = onPause, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Pause")
                        }
                    }
                    state.isPaused -> {
                        Button(onClick = onResume, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Resume")
                        }
                    }
                    else -> {
                        Button(onClick = { onStart(state.mode) }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Start")
                        }
                    }
                }
                Button(
                    onClick = onCancel,
                    enabled = state.isRunning || state.isPaused,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Cancel")
                }
                AssistChip(
                    onClick = onQuickSettingsGuide,
                    label = { Text(text = "MIUI battery guidance") },
                    colors = AssistChipDefaults.assistChipColors()
                )
            }
        }
    }
}

@Composable
private fun ModeChip(title: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = title) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    )
}
