package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.repository.TimerRepository
import javax.inject.Inject

class PauseTimerUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke() {
        timerRepository.pauseTimer()
    }
}
