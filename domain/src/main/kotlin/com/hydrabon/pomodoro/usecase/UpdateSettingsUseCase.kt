package com.hydrabon.pomodoro.usecase

import com.hydrabon.pomodoro.model.PomodoroSettings
import com.hydrabon.pomodoro.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: PomodoroSettings) {
        settingsRepository.updateSettings(settings)
    }
}
