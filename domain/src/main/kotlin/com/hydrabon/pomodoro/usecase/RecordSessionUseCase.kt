package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.PomodoroSession
import com.hydrabon.pomodoro.repository.SessionRepository
import javax.inject.Inject

class RecordSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(session: PomodoroSession) {
        sessionRepository.upsertSession(session)
    }
}
