package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.TimerMode
import com.hydrabon.pomodoro.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class StartTimerUseCaseTest {

    @Test
    fun `start timer delegates to repository`() = runBlocking {
        val repository = FakeTimerRepository()
        val useCase = StartTimerUseCase(repository)

        useCase(TimerMode.SHORT_BREAK)

        assertEquals(TimerMode.SHORT_BREAK, repository.startedMode)
    }

    private class FakeTimerRepository : TimerRepository {
        var startedMode: TimerMode? = null
        override val timerState: Flow<com.hydrabon.pomodoro.model.TimerState> = emptyFlow()
        override suspend fun startTimer(mode: TimerMode) {
            startedMode = mode
        }
        override suspend fun pauseTimer() = Unit
        override suspend fun resumeTimer() = Unit
        override suspend fun cancelTimer() = Unit
    }
}
