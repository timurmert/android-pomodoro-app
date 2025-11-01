package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.PomodoroSettings
import com.hydrabon.pomodoro.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<PomodoroSettings> = settingsRepository.observeSettings()
}
