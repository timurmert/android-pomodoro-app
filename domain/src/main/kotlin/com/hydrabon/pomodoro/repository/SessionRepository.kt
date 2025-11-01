package com.hydrabon.pomodoro.repository

import com.hydrabon.pomodoro.model.DailyStats
import com.hydrabon.pomodoro.model.PomodoroDefaults
import com.hydrabon.pomodoro.model.PomodoroSession
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId

interface SessionRepository {
    suspend fun upsertSession(session: PomodoroSession)
    fun observeTodayStats(zoneId: ZoneId = PomodoroDefaults.TIME_ZONE): Flow<DailyStats>
    fun observeWeeklyStats(zoneId: ZoneId = PomodoroDefaults.TIME_ZONE): Flow<List<DailyStats>>
}
