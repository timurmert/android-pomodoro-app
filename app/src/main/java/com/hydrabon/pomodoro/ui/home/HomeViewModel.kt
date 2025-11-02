package com.hydrabon.pomodoro.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hydrabon.pomodoro.model.TimerMode
import com.hydrabon.pomodoro.usecase.CancelTimerUseCase
import com.hydrabon.pomodoro.usecase.ObserveSettingsUseCase
import com.hydrabon.pomodoro.usecase.ObserveTimerStateUseCase
import com.hydrabon.pomodoro.usecase.ObserveTodayStatsUseCase
import com.hydrabon.pomodoro.usecase.PauseTimerUseCase
import com.hydrabon.pomodoro.usecase.ResumeTimerUseCase
import com.hydrabon.pomodoro.usecase.StartTimerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeTimerStateUseCase: ObserveTimerStateUseCase,
    observeTodayStatsUseCase: ObserveTodayStatsUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val pauseTimerUseCase: PauseTimerUseCase,
    private val resumeTimerUseCase: ResumeTimerUseCase,
    private val cancelTimerUseCase: CancelTimerUseCase
) : ViewModel() {

    private val timerStateFlow = observeTimerStateUseCase()
    private val todayStatsFlow = observeTodayStatsUseCase()
    private val settingsFlow = observeSettingsUseCase()

    val state: StateFlow<HomeUiState> = combine(timerStateFlow, todayStatsFlow, settingsFlow) { timerState, todayStats, settings ->
        val defaultDuration = when (timerState.mode) {
            TimerMode.FOCUS -> settings.focusDuration
            TimerMode.SHORT_BREAK -> settings.shortBreakDuration
            TimerMode.LONG_BREAK -> settings.longBreakDuration
        }
        val remaining = if (timerState.remaining == Duration.ZERO) defaultDuration else timerState.remaining
        HomeUiState(
            mode = timerState.mode,
            remainingLabel = remaining.formatMinutesSeconds(),
            isRunning = timerState.isRunning,
            isPaused = timerState.isPaused,
            completedSessions = todayStats.completedFocusSessions,
            targetSessions = timerState.targetFocusSessions.takeIf { it > 0 } ?: computeTarget(settings.focusDuration, settings.dailyFocusGoalMinutes),
            todayFocusMinutes = todayStats.totalFocusDurationMinutes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun startTimer(mode: TimerMode = TimerMode.FOCUS) {
        viewModelScope.launch {
            startTimerUseCase(mode)
        }
    }

    fun pause() {
        viewModelScope.launch { pauseTimerUseCase() }
    }

    fun resume() {
        viewModelScope.launch { resumeTimerUseCase() }
    }

    fun cancel() {
        viewModelScope.launch { cancelTimerUseCase() }
    }

    private fun Duration.formatMinutesSeconds(): String {
        val totalSeconds = inWholeSeconds
        val minutes = (totalSeconds / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        return "%02d:%02d".format(minutes, seconds)
    }

    private fun computeTarget(duration: Duration, goalMinutes: Int): Int {
        val perSession = duration.inWholeMinutes
            .coerceAtLeast(1)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
        return (goalMinutes / perSession).coerceAtLeast(1)
    }
}

data class HomeUiState(
    val mode: TimerMode = TimerMode.FOCUS,
    val remainingLabel: String = "25:00",
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val completedSessions: Int = 0,
    val targetSessions: Int = 4,
    val todayFocusMinutes: Int = 0
)
