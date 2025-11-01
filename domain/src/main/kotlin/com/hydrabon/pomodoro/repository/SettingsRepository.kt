package com.hydrabon.pomodoro.repository

import com.hydrabon.pomodoro.model.PomodoroSettings
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface SettingsRepository {
    fun observeSettings(): Flow<PomodoroSettings>
    suspend fun updateSettings(settings: PomodoroSettings)
    suspend fun updateFocusDuration(duration: Duration)
    suspend fun updateShortBreakDuration(duration: Duration)
    suspend fun updateLongBreakDuration(duration: Duration)
    suspend fun updateSessionsBeforeLongBreak(count: Int)
    suspend fun updateDailyGoal(minutes: Int)
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setAutoStartBreaks(enabled: Boolean)
    suspend fun setAutoStartPomodoro(enabled: Boolean)
}
