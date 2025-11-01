package com.hydrabon.pomodoro.data.session

import com.hydrabon.pomodoro.model.DailyStats
import com.hydrabon.pomodoro.model.PomodoroSession
import com.hydrabon.pomodoro.model.TimerMode
import com.hydrabon.pomodoro.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    override suspend fun upsertSession(session: PomodoroSession) {
        val entity = SessionEntity(
            id = if (session.id == 0L) 0L else session.id,
            mode = session.mode,
            startedAtEpochMillis = session.startedAt.toEpochMilli(),
            endedAtEpochMillis = session.endedAt.toEpochMilli(),
            completed = session.completed
        )
        sessionDao.upsert(entity)
    }

    override fun observeTodayStats(zoneId: ZoneId): Flow<DailyStats> {
        val targetZone = zoneId
        val todayStart = ZonedDateTime.now(targetZone).toLocalDate().atStartOfDay(targetZone)
        val from = todayStart.toInstant().toEpochMilli()
        val to = todayStart.plusDays(1).minusNanos(1).toInstant().toEpochMilli()
        return sessionDao.observeSessionsBetween(from, to).map { sessions ->
            sessions.asDomainDailyStats(targetZone)
        }
    }

    override fun observeWeeklyStats(zoneId: ZoneId): Flow<List<DailyStats>> {
        val targetZone = zoneId
        val today = ZonedDateTime.now(targetZone).toLocalDate()
        val startOfWeek = today.minusDays(6)
        val from = startOfWeek.atStartOfDay(targetZone).toInstant().toEpochMilli()
        return sessionDao.observeSessionsFrom(from).map { sessions ->
            val grouped = sessions.groupBy { entity ->
                ZonedDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(entity.startedAtEpochMillis),
                    targetZone
                ).toLocalDate()
            }
            (0..6).map { offset ->
                val day = startOfWeek.plusDays(offset.toLong())
                val daySessions = grouped[day].orEmpty()
                daySessions.asDomainDailyStats(targetZone, day)
            }
        }
    }

    private fun List<SessionEntity>.asDomainDailyStats(
        zoneId: ZoneId,
        dateOverride: java.time.LocalDate? = null
    ): DailyStats {
        val focusSessions = filter { it.mode == TimerMode.FOCUS && it.completed }
        val totalFocusMinutes = focusSessions.sumOf { entity ->
            Duration.ofMillis(entity.endedAtEpochMillis - entity.startedAtEpochMillis).toMinutes().toInt()
        }
        val date = dateOverride ?: if (isNotEmpty()) {
            ZonedDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(first().startedAtEpochMillis),
                zoneId
            ).toLocalDate()
        } else {
            ZonedDateTime.now(zoneId).toLocalDate()
        }
        return DailyStats(
            date = date,
            completedFocusSessions = focusSessions.size,
            totalFocusDurationMinutes = totalFocusMinutes
        )
    }
}
