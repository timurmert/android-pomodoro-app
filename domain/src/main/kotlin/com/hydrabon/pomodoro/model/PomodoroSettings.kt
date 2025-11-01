package com.hydrabon.pomodoro.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class PomodoroSettings(
    val focusDuration: Duration = 25.minutes,
    val shortBreakDuration: Duration = 5.minutes,
    val longBreakDuration: Duration = 15.minutes,
    val sessionsBeforeLongBreak: Int = 4,
    val dailyFocusGoalMinutes: Int = 150,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoro: Boolean = false
)
