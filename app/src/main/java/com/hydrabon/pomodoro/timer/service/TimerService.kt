package com.hydrabon.pomodoro.timer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.hydrabon.pomodoro.model.PomodoroSession
import com.hydrabon.pomodoro.model.TimerMode
import com.hydrabon.pomodoro.timer.ActiveTimerState
import com.hydrabon.pomodoro.timer.ActiveTimerStorage
import com.hydrabon.pomodoro.timer.TimerNotificationFactory
import com.hydrabon.pomodoro.timer.TimerStateHolder
import com.hydrabon.pomodoro.timer.AlarmScheduler
import com.hydrabon.pomodoro.usecase.RecordSessionUseCase
import com.hydrabon.pomodoro.usecase.StartTimerUseCase
import com.hydrabon.pomodoro.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant

@AndroidEntryPoint
class TimerService : Service() {

    @Inject lateinit var timerStateHolder: TimerStateHolder
    @Inject lateinit var notificationFactory: TimerNotificationFactory
    @Inject lateinit var activeTimerStorage: ActiveTimerStorage
    @Inject lateinit var recordSessionUseCase: RecordSessionUseCase
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var startTimerUseCase: StartTimerUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tickerJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START, ACTION_RESUME -> {
                val mode = intent.getStringExtra(EXTRA_MODE)?.let { TimerMode.valueOf(it) } ?: return START_NOT_STICKY
                val duration = intent.getLongExtra(EXTRA_DURATION_MILLIS, 0L)
                val expectedEnd = intent.getLongExtra(EXTRA_EXPECTED_END, 0L)
                startTimer(mode, duration, expectedEnd)
            }
            ACTION_PAUSE -> pause()
            ACTION_CANCEL -> cancel()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startTimer(mode: TimerMode, durationMillis: Long, expectedEndMillis: Long) {
        val startMillis = expectedEndMillis - durationMillis
        val startInstant = Instant.ofEpochMilli(startMillis)
        val expectedEnd = Instant.ofEpochMilli(expectedEndMillis)
        val initialRemaining = (expectedEndMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        startForeground(
            TimerNotificationFactory.NOTIFICATION_ID,
            notificationFactory.build(mode, initialRemaining, isPaused = false)
        )
        tickerJob?.cancel()
        tickerJob = serviceScope.launch {
            var remaining = initialRemaining
            while (isActive && remaining > 0) {
                timerStateHolder.update { current ->
                    current.copy(
                        mode = mode,
                        remaining = remaining.milliseconds,
                        isRunning = true,
                        isPaused = false,
                        startedAt = current.startedAt ?: startInstant,
                        expectedEndAt = expectedEnd
                    )
                }
                activeTimerStorage.save(
                    ActiveTimerState(
                        mode = mode,
                        startedAt = startInstant,
                        expectedEndAt = expectedEnd,
                        durationMillis = durationMillis,
                        remainingMillis = remaining,
                        isPaused = false
                    )
                )
                NotificationManagerCompat.from(this@TimerService).notify(
                    TimerNotificationFactory.NOTIFICATION_ID,
                    notificationFactory.build(mode, remaining, isPaused = false)
                )
                delay(TICK_INTERVAL_MILLIS)
                remaining = (expectedEndMillis - System.currentTimeMillis()).coerceAtLeast(0L)
            }
            if (remaining <= 0) {
                onTimerCompleted(mode, startInstant, expectedEnd)
            }
        }
    }

    private fun pause() {
        tickerJob?.cancel()
        serviceScope.launch {
            val state = timerStateHolder.current
            val remaining = state.remaining.inWholeMilliseconds
            activeTimerStorage.save(
                ActiveTimerState(
                    mode = state.mode,
                    startedAt = state.startedAt ?: Instant.now(),
                    expectedEndAt = state.expectedEndAt ?: Instant.now(),
                    durationMillis = state.remaining.inWholeMilliseconds,
                    remainingMillis = remaining,
                    isPaused = true
                )
            )
            NotificationManagerCompat.from(this@TimerService).notify(
                TimerNotificationFactory.NOTIFICATION_ID,
                notificationFactory.build(state.mode, remaining, isPaused = true)
            )
        }
    }

    private fun cancel() {
        tickerJob?.cancel()
        activeTimerStorage.clear()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private suspend fun onTimerCompleted(mode: TimerMode, startedAt: Instant, endedAt: Instant) {
        recordSessionUseCase(
            PomodoroSession(
                mode = mode,
                startedAt = startedAt,
                endedAt = endedAt,
                completed = true
            )
        )
        alarmScheduler.cancel()
        activeTimerStorage.clear()
        timerStateHolder.update { current ->
            current.copy(
                isRunning = false,
                isPaused = false,
                remaining = Duration.ZERO,
                expectedEndAt = null,
                startedAt = null,
                completedFocusSessionsToday = if (mode == TimerMode.FOCUS) current.completedFocusSessionsToday + 1 else current.completedFocusSessionsToday
            )
        }
        NotificationManagerCompat.from(this).cancel(TimerNotificationFactory.NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        maybeAutoStartNext(mode)
        stopSelf()
    }

    private suspend fun maybeAutoStartNext(mode: TimerMode) {
        val settings = settingsRepository.observeSettings().first()
        if (mode != TimerMode.FOCUS) {
            if (settings.autoStartPomodoro) {
                startTimerUseCase(TimerMode.FOCUS)
            }
            return
        }
        val completed = timerStateHolder.current.completedFocusSessionsToday
        val isLongBreak = completed % settings.sessionsBeforeLongBreak == 0
        val nextMode = if (isLongBreak) TimerMode.LONG_BREAK else TimerMode.SHORT_BREAK
        if (settings.autoStartBreaks) {
            startTimerUseCase(nextMode)
        }
    }

    companion object {
        const val ACTION_START = "com.hydrabon.pomodoro.action.START"
        const val ACTION_PAUSE = "com.hydrabon.pomodoro.action.PAUSE"
        const val ACTION_RESUME = "com.hydrabon.pomodoro.action.RESUME"
        const val ACTION_CANCEL = "com.hydrabon.pomodoro.action.CANCEL"
        private const val EXTRA_MODE = "extra_mode"
        private const val EXTRA_DURATION_MILLIS = "extra_duration"
        private const val EXTRA_EXPECTED_END = "extra_expected_end"
        private const val TICK_INTERVAL_MILLIS = 1000L

        fun intent(
            context: Context,
            mode: TimerMode,
            durationMillis: Long,
            expectedEndMillis: Long
        ): Intent = Intent(context, TimerService::class.java).apply {
            putExtra(EXTRA_MODE, mode.name)
            putExtra(EXTRA_DURATION_MILLIS, durationMillis)
            putExtra(EXTRA_EXPECTED_END, expectedEndMillis)
        }
    }
}
