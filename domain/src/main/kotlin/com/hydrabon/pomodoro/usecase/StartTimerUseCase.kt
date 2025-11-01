package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.TimerMode
import com.hydrabon.pomodoro.repository.TimerRepository
import javax.inject.Inject

class StartTimerUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(mode: TimerMode) {
        timerRepository.startTimer(mode)
    }
}
