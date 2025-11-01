package com.hydrabon.pomodoro.repository

import com.hydrabon.pomodoro.model.TimerMode
import com.hydrabon.pomodoro.model.TimerState
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    val timerState: Flow<TimerState>
    suspend fun startTimer(mode: TimerMode)
    suspend fun pauseTimer()
    suspend fun resumeTimer()
    suspend fun cancelTimer()
}
