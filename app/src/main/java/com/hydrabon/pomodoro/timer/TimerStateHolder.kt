package com.hydrabon.pomodoro.timer

import com.hydrabon.pomodoro.model.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerStateHolder @Inject constructor() {
    private val mutableState = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = mutableState.asStateFlow()
    val current: TimerState get() = mutableState.value

    fun update(reducer: (TimerState) -> TimerState) {
        mutableState.value = reducer(mutableState.value)
    }

    fun set(state: TimerState) {
        mutableState.value = state
    }
}
