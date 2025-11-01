package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.DailyStats
import com.hydrabon.pomodoro.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWeeklyStatsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<List<DailyStats>> = sessionRepository.observeWeeklyStats()
}
