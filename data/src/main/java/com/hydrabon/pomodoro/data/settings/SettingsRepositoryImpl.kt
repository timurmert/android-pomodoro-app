package com.hydrabon.pomodoro.data.settings

import com.hydrabon.pomodoro.model.PomodoroSettings
import com.hydrabon.pomodoro.data.settings.Keys
import com.hydrabon.pomodoro.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Duration

class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override fun observeSettings(): Flow<PomodoroSettings> = dataSource.settings

    override suspend fun updateSettings(settings: PomodoroSettings) {
        dataSource.update {
            this[Keys.focusMinutes] = settings.focusDuration
            this[Keys.shortBreakMinutes] = settings.shortBreakDuration
            this[Keys.longBreakMinutes] = settings.longBreakDuration
            this[Keys.sessionsBeforeLongBreak] = settings.sessionsBeforeLongBreak
            this[Keys.dailyGoalMinutes] = settings.dailyFocusGoalMinutes
            this[Keys.soundEnabled] = settings.soundEnabled
            this[Keys.vibrationEnabled] = settings.vibrationEnabled
            this[Keys.autoStartBreaks] = settings.autoStartBreaks
            this[Keys.autoStartPomodoro] = settings.autoStartPomodoro
        }
    }

    override suspend fun updateFocusDuration(duration: Duration) {
        dataSource.update { this[Keys.focusMinutes] = duration }
    }

    override suspend fun updateShortBreakDuration(duration: Duration) {
        dataSource.update { this[Keys.shortBreakMinutes] = duration }
    }

    override suspend fun updateLongBreakDuration(duration: Duration) {
        dataSource.update { this[Keys.longBreakMinutes] = duration }
    }

    override suspend fun updateSessionsBeforeLongBreak(count: Int) {
        dataSource.update { this[Keys.sessionsBeforeLongBreak] = count }
    }

    override suspend fun updateDailyGoal(minutes: Int) {
        dataSource.update { this[Keys.dailyGoalMinutes] = minutes }
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        dataSource.update { this[Keys.soundEnabled] = enabled }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        dataSource.update { this[Keys.vibrationEnabled] = enabled }
    }

    override suspend fun setAutoStartBreaks(enabled: Boolean) {
        dataSource.update { this[Keys.autoStartBreaks] = enabled }
    }

    override suspend fun setAutoStartPomodoro(enabled: Boolean) {
        dataSource.update { this[Keys.autoStartPomodoro] = enabled }
    }
}
