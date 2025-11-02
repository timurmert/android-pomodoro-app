package com.hydrabon.pomodoro.timer

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.hydrabon.pomodoro.di.ApplicationScope
import com.hydrabon.pomodoro.model.TimerMode
import com.hydrabon.pomodoro.model.TimerState
import com.hydrabon.pomodoro.repository.SettingsRepository
import com.hydrabon.pomodoro.repository.TimerRepository
import com.hydrabon.pomodoro.timer.service.TimerService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Singleton
class TimerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val holder: TimerStateHolder,
    private val settingsRepository: SettingsRepository,
    private val alarmScheduler: AlarmScheduler,
    private val activeTimerStorage: ActiveTimerStorage,
    @ApplicationScope private val applicationScope: CoroutineScope
) : TimerRepository {

    override val timerState: Flow<TimerState> = holder.state

    init {
        applicationScope.launch {
            val stored = activeTimerStorage.activeTimer.firstOrNull()
            if (stored != null) {
                val restored = TimerState(
                    mode = stored.mode,
                    remaining = stored.remainingMillis.milliseconds,
                    isRunning = !stored.isPaused,
                    isPaused = stored.isPaused,
                    startedAt = stored.startedAt,
                    expectedEndAt = stored.expectedEndAt
                )
                holder.set(restored)
                if (!stored.isPaused) {
                    ensureServiceRunning(restored)
                }
            }
        }
    }

    override suspend fun startTimer(mode: TimerMode) {
        val settings = settingsRepository.observeSettings().first()
        val duration = when (mode) {
            TimerMode.FOCUS -> settings.focusDuration
            TimerMode.SHORT_BREAK -> settings.shortBreakDuration
            TimerMode.LONG_BREAK -> settings.longBreakDuration
        }
        val start = Instant.now()
        val expectedEnd = start.plusMillis(duration.inWholeMilliseconds)
        val targetSessions = computeDailyTarget(settings.focusDuration, settings.dailyFocusGoalMinutes)
        holder.set(
            TimerState(
                mode = mode,
                remaining = duration,
                isRunning = true,
                isPaused = false,
                startedAt = start,
                expectedEndAt = expectedEnd,
                completedFocusSessionsToday = holder.state.value.completedFocusSessionsToday,
                targetFocusSessions = targetSessions
            )
        )
        alarmScheduler.schedule(mode, expectedEnd)
        activeTimerStorage.save(
            ActiveTimerState(
                mode = mode,
                startedAt = start,
                expectedEndAt = expectedEnd,
                durationMillis = duration.inWholeMilliseconds,
                remainingMillis = duration.inWholeMilliseconds,
                isPaused = false
            )
        )
        startService(TimerService.ACTION_START, mode, duration.inWholeMilliseconds, expectedEnd.toEpochMilli())
    }

    override suspend fun pauseTimer() {
        val current = holder.state.value
        if (!current.isRunning || current.isPaused) return
        val updated = current.copy(isRunning = false, isPaused = true)
        holder.set(updated)
        alarmScheduler.cancel()
        val stored = activeTimerStorage.activeTimer.firstOrNull()
        val durationMillis = stored?.durationMillis ?: current.remaining.inWholeMilliseconds
        activeTimerStorage.save(
            ActiveTimerState(
                mode = updated.mode,
                startedAt = updated.startedAt ?: Instant.now(),
                expectedEndAt = updated.expectedEndAt ?: Instant.now(),
                durationMillis = durationMillis,
                remainingMillis = updated.remaining.inWholeMilliseconds,
                isPaused = true
            )
        )
        startService(TimerService.ACTION_PAUSE, updated.mode, updated.remaining.inWholeMilliseconds, updated.expectedEndAt?.toEpochMilli() ?: 0L)
    }

    override suspend fun resumeTimer() {
        val current = holder.state.value
        if (!current.isPaused) return
        val remainingMillis = current.remaining.inWholeMilliseconds
        val newExpectedEnd = Instant.now().plusMillis(remainingMillis)
        val updated = current.copy(isRunning = true, isPaused = false, expectedEndAt = newExpectedEnd)
        holder.set(updated)
        alarmScheduler.schedule(updated.mode, newExpectedEnd)
        val stored = activeTimerStorage.activeTimer.firstOrNull()
        val durationMillis = stored?.durationMillis ?: remainingMillis
        activeTimerStorage.save(
            ActiveTimerState(
                mode = updated.mode,
                startedAt = updated.startedAt ?: Instant.now(),
                expectedEndAt = newExpectedEnd,
                durationMillis = durationMillis,
                remainingMillis = remainingMillis,
                isPaused = false
            )
        )
        startService(TimerService.ACTION_RESUME, updated.mode, remainingMillis, newExpectedEnd.toEpochMilli())
    }

    override suspend fun cancelTimer() {
        holder.set(TimerState())
        alarmScheduler.cancel()
        activeTimerStorage.clear()
        startService(TimerService.ACTION_CANCEL)
    }

    private fun startService(action: String, mode: TimerMode, durationMillis: Long, expectedEndMillis: Long) {
        val intent = TimerService.intent(context, mode, durationMillis, expectedEndMillis).apply {
            this.action = action
        }
        ContextCompat.startForegroundService(context, intent)
    }

    private fun startService(action: String) {
        val intent = Intent(context, TimerService::class.java).apply {
            this.action = action
        }
        ContextCompat.startForegroundService(context, intent)
    }

    private fun ensureServiceRunning(state: TimerState) {
        startService(
            action = TimerService.ACTION_START,
            mode = state.mode,
            durationMillis = state.remaining.inWholeMilliseconds,
            expectedEndMillis = state.expectedEndAt?.toEpochMilli() ?: Instant.now().toEpochMilli()
        )
    }

    private fun computeDailyTarget(focusDuration: Duration, goalMinutes: Int): Int {
        val minutesPerSession = focusDuration.inWholeMinutes
            .coerceAtLeast(1)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
        return (goalMinutes / minutesPerSession).coerceAtLeast(1)
    }
}
