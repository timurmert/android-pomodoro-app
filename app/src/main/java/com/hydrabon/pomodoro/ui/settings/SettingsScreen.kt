package com.hydrabon.pomodoro.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    onShowMiuiGuide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Durations (minutes)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        NumberField(
            label = "Focus",
            value = state.focusMinutes,
            onValueChange = { onEvent(SettingsEvent.FocusDuration(it)) }
        )
        NumberField(
            label = "Short break",
            value = state.shortBreakMinutes,
            onValueChange = { onEvent(SettingsEvent.ShortBreakDuration(it)) }
        )
        NumberField(
            label = "Long break",
            value = state.longBreakMinutes,
            onValueChange = { onEvent(SettingsEvent.LongBreakDuration(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Goals", style = MaterialTheme.typography.titleMedium)
        NumberField(
            label = "Sessions before long break",
            value = state.sessionsBeforeLongBreak,
            onValueChange = { onEvent(SettingsEvent.SessionsBeforeLongBreak(it)) }
        )
        NumberField(
            label = "Daily focus goal (min)",
            value = state.dailyGoalMinutes,
            onValueChange = { onEvent(SettingsEvent.DailyGoalMinutes(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Automation", style = MaterialTheme.typography.titleMedium)
        ToggleRow(
            title = "Auto-start breaks",
            checked = state.autoStartBreaks,
            onCheckedChange = { onEvent(SettingsEvent.AutoStartBreaks(it)) }
        )
        ToggleRow(
            title = "Auto-start focus",
            checked = state.autoStartPomodoro,
            onCheckedChange = { onEvent(SettingsEvent.AutoStartPomodoro(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Notifications", style = MaterialTheme.typography.titleMedium)
        ToggleRow(
            title = "Sound",
            checked = state.soundEnabled,
            onCheckedChange = { onEvent(SettingsEvent.SoundEnabled(it)) }
        )
        ToggleRow(
            title = "Vibration",
            checked = state.vibrationEnabled,
            onCheckedChange = { onEvent(SettingsEvent.VibrationEnabled(it)) }
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onShowMiuiGuide, modifier = Modifier.fillMaxWidth()) {
            Text(text = "MIUI battery & autostart guide")
        }
    }
}

@Composable
private fun NumberField(label: String, value: Int, onValueChange: (Int) -> Unit) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { input ->
            input.toIntOrNull()?.let(onValueChange)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
