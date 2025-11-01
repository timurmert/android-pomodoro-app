package com.hydrabon.pomodoro.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hydrabon.pomodoro.model.PomodoroSettings
import com.hydrabon.pomodoro.usecase.ObserveSettingsUseCase
import com.hydrabon.pomodoro.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val settingsFlow = observeSettingsUseCase()

    val state: StateFlow<SettingsUiState> = settingsFlow
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            val current = settingsFlow.first()
            val updated = when (event) {
                is SettingsEvent.FocusDuration -> current.copy(focusDuration = event.minutes.minutes)
                is SettingsEvent.ShortBreakDuration -> current.copy(shortBreakDuration = event.minutes.minutes)
                is SettingsEvent.LongBreakDuration -> current.copy(longBreakDuration = event.minutes.minutes)
                is SettingsEvent.SessionsBeforeLongBreak -> current.copy(sessionsBeforeLongBreak = event.count)
                is SettingsEvent.DailyGoalMinutes -> current.copy(dailyFocusGoalMinutes = event.minutes)
                is SettingsEvent.SoundEnabled -> current.copy(soundEnabled = event.enabled)
                is SettingsEvent.VibrationEnabled -> current.copy(vibrationEnabled = event.enabled)
                is SettingsEvent.AutoStartBreaks -> current.copy(autoStartBreaks = event.enabled)
                is SettingsEvent.AutoStartPomodoro -> current.copy(autoStartPomodoro = event.enabled)
            }
            updateSettingsUseCase(updated)
        }
    }
}

data class SettingsUiState(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
    val dailyGoalMinutes: Int = 150,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoro: Boolean = false
)

sealed interface SettingsEvent {
    data class FocusDuration(val minutes: Int) : SettingsEvent
    data class ShortBreakDuration(val minutes: Int) : SettingsEvent
    data class LongBreakDuration(val minutes: Int) : SettingsEvent
    data class SessionsBeforeLongBreak(val count: Int) : SettingsEvent
    data class DailyGoalMinutes(val minutes: Int) : SettingsEvent
    data class SoundEnabled(val enabled: Boolean) : SettingsEvent
    data class VibrationEnabled(val enabled: Boolean) : SettingsEvent
    data class AutoStartBreaks(val enabled: Boolean) : SettingsEvent
    data class AutoStartPomodoro(val enabled: Boolean) : SettingsEvent
}

private fun PomodoroSettings.toUiState(): SettingsUiState = SettingsUiState(
    focusMinutes = focusDuration.inWholeMinutes.toInt(),
    shortBreakMinutes = shortBreakDuration.inWholeMinutes.toInt(),
    longBreakMinutes = longBreakDuration.inWholeMinutes.toInt(),
    sessionsBeforeLongBreak = sessionsBeforeLongBreak,
    dailyGoalMinutes = dailyFocusGoalMinutes,
    soundEnabled = soundEnabled,
    vibrationEnabled = vibrationEnabled,
    autoStartBreaks = autoStartBreaks,
    autoStartPomodoro = autoStartPomodoro
)
