package com.hydrabon.pomodoro.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hydrabon.pomodoro.model.DailyStats
import com.hydrabon.pomodoro.usecase.ObserveWeeklyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class StatsViewModel @Inject constructor(
    observeWeeklyStatsUseCase: ObserveWeeklyStatsUseCase
) : ViewModel() {

    val state: StateFlow<StatsUiState> = observeWeeklyStatsUseCase()
        .map { StatsUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())
}

data class StatsUiState(
    val dailyStats: List<DailyStats> = emptyList()
)
