package com.hydrabon.pomodoro.model

import java.time.LocalDate

/**
 * Aggregated focus outcome for a day in the configured timezone.
 */
data class DailyStats(
    val date: LocalDate,
    val completedFocusSessions: Int,
    val totalFocusDurationMinutes: Int
)
