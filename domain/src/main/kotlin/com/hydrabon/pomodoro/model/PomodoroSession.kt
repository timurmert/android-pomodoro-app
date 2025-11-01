package com.hydrabon.pomodoro.model

import java.time.Duration
import java.time.Instant

/**
 * Represents a persisted Pomodoro session instance.
 */
data class PomodoroSession(
    val id: Long = 0L,
    val mode: TimerMode,
    val startedAt: Instant,
    val endedAt: Instant,
    val completed: Boolean
) {
    val duration: Duration = Duration.between(startedAt, endedAt)
}
