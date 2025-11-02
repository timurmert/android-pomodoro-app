package com.hydrabon.pomodoro.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hydrabon.pomodoro.model.PomodoroSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private const val DATA_STORE_NAME = "pomodoro_settings"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

class SettingsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val settings: Flow<PomodoroSettings> = context.settingsDataStore.data.map { prefs ->
        prefs.toSettings()
    }

    suspend fun update(block: MutablePreferencesScope.() -> Unit) {
        context.settingsDataStore.edit { prefs ->
            MutablePreferencesScope(prefs).block()
        }
    }

    class MutablePreferencesScope internal constructor(private val prefs: androidx.datastore.preferences.core.MutablePreferences) {
        operator fun set(key: Preferences.Key<Int>, value: Duration) {
            prefs[key] = value.inWholeMinutes.toInt()
        }

        operator fun set(key: Preferences.Key<Int>, value: Int) {
            prefs[key] = value
        }

        operator fun set(key: Preferences.Key<Boolean>, value: Boolean) {
            prefs[key] = value
        }
    }
}

internal object Keys {
    val focusMinutes = intPreferencesKey("focus_minutes")
    val shortBreakMinutes = intPreferencesKey("short_break_minutes")
    val longBreakMinutes = intPreferencesKey("long_break_minutes")
    val sessionsBeforeLongBreak = intPreferencesKey("sessions_before_long_break")
    val dailyGoalMinutes = intPreferencesKey("daily_goal_minutes")
    val soundEnabled = booleanPreferencesKey("sound_enabled")
    val vibrationEnabled = booleanPreferencesKey("vibration_enabled")
    val autoStartBreaks = booleanPreferencesKey("auto_start_breaks")
    val autoStartPomodoro = booleanPreferencesKey("auto_start_pomodoro")
}

private fun Preferences.toSettings(): PomodoroSettings {
    val defaults = PomodoroSettings()
    return PomodoroSettings(
        focusDuration = get(Keys.focusMinutes)?.minutes ?: defaults.focusDuration,
        shortBreakDuration = get(Keys.shortBreakMinutes)?.minutes ?: defaults.shortBreakDuration,
        longBreakDuration = get(Keys.longBreakMinutes)?.minutes ?: defaults.longBreakDuration,
        sessionsBeforeLongBreak = get(Keys.sessionsBeforeLongBreak) ?: defaults.sessionsBeforeLongBreak,
        dailyFocusGoalMinutes = get(Keys.dailyGoalMinutes) ?: defaults.dailyFocusGoalMinutes,
        soundEnabled = get(Keys.soundEnabled) ?: defaults.soundEnabled,
        vibrationEnabled = get(Keys.vibrationEnabled) ?: defaults.vibrationEnabled,
        autoStartBreaks = get(Keys.autoStartBreaks) ?: defaults.autoStartBreaks,
        autoStartPomodoro = get(Keys.autoStartPomodoro) ?: defaults.autoStartPomodoro
    )
}
