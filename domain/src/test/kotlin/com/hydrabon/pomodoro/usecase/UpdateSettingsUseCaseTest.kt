package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.PomodoroSettings
import com.hydrabon.pomodoro.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class UpdateSettingsUseCaseTest {

    @Test
    fun `update settings persists new values`() = runBlocking {
        val repository = FakeSettingsRepository()
        val useCase = UpdateSettingsUseCase(repository)
        val newSettings = PomodoroSettings(focusDuration = 30.minutes)

        useCase(newSettings)

        assertTrue(repository.lastUpdate === newSettings)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val flow = MutableStateFlow(PomodoroSettings())
        var lastUpdate: PomodoroSettings? = null
        override fun observeSettings(): Flow<PomodoroSettings> = flow.asStateFlow()
        override suspend fun updateSettings(settings: PomodoroSettings) {
            lastUpdate = settings
            flow.value = settings
        }
        override suspend fun updateFocusDuration(duration: Duration) { }
        override suspend fun updateShortBreakDuration(duration: Duration) { }
        override suspend fun updateLongBreakDuration(duration: Duration) { }
        override suspend fun updateSessionsBeforeLongBreak(count: Int) { }
        override suspend fun updateDailyGoal(minutes: Int) { }
        override suspend fun setSoundEnabled(enabled: Boolean) { }
        override suspend fun setVibrationEnabled(enabled: Boolean) { }
        override suspend fun setAutoStartBreaks(enabled: Boolean) { }
        override suspend fun setAutoStartPomodoro(enabled: Boolean) { }
    }
}
