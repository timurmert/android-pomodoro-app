package com.hydrabon.pomodoro.timer

import com.hydrabon.pomodoro.model.TimerMode
import java.time.Instant

data class ActiveTimerState(
    val mode: TimerMode,
    val startedAt: Instant,
    val expectedEndAt: Instant,
    val durationMillis: Long,
    val remainingMillis: Long,
    val isPaused: Boolean
)
