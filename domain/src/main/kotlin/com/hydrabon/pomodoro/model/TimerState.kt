package com.hydrabon.pomodoro.model

import java.time.Instant
import kotlin.time.Duration

/**
 * Immutable snapshot of the active Pomodoro timer.
 */
data class TimerState(
    val mode: TimerMode = TimerMode.FOCUS,
    val remaining: Duration = Duration.ZERO,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val completedFocusSessionsToday: Int = 0,
    val targetFocusSessions: Int = 0,
    val startedAt: Instant? = null,
    val expectedEndAt: Instant? = null
) {
    val isIdle: Boolean get() = !isRunning && remaining == Duration.ZERO
}
