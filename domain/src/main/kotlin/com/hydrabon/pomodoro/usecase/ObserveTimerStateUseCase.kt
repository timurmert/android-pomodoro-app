package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.TimerState
import com.hydrabon.pomodoro.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTimerStateUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    operator fun invoke(): Flow<TimerState> = timerRepository.timerState
}
