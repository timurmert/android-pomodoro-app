package com.hydrabon.pomodoro.timer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

private const val TIMER_STORE_NAME = "active_timer"

private val Context.activeTimerDataStore: DataStore<Preferences> by preferencesDataStore(name = TIMER_STORE_NAME)

class ActiveTimerStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val activeTimer: Flow<ActiveTimerState?> = context.activeTimerDataStore.data.map { prefs ->
        val modeValue = prefs[Keys.mode] ?: return@map null
        ActiveTimerState(
            mode = com.hydrabon.pomodoro.model.TimerMode.valueOf(modeValue),
            startedAt = Instant.ofEpochMilli(prefs[Keys.startedAt] ?: return@map null),
            expectedEndAt = Instant.ofEpochMilli(prefs[Keys.expectedEnd] ?: return@map null),
            durationMillis = prefs[Keys.durationMillis] ?: return@map null,
            remainingMillis = prefs[Keys.remainingMillis] ?: prefs[Keys.durationMillis] ?: return@map null,
            isPaused = prefs[Keys.isPaused] ?: false
        )
    }

    suspend fun save(state: ActiveTimerState) {
        context.activeTimerDataStore.edit { prefs ->
            prefs[Keys.mode] = state.mode.name
            prefs[Keys.startedAt] = state.startedAt.toEpochMilli()
            prefs[Keys.expectedEnd] = state.expectedEndAt.toEpochMilli()
            prefs[Keys.durationMillis] = state.durationMillis
            prefs[Keys.remainingMillis] = state.remainingMillis
            prefs[Keys.isPaused] = state.isPaused
        }
    }

    suspend fun updateRemaining(remainingMillis: Long) {
        context.activeTimerDataStore.edit { prefs ->
            prefs[Keys.remainingMillis] = remainingMillis
        }
    }

    suspend fun setPaused(paused: Boolean) {
        context.activeTimerDataStore.edit { prefs ->
            prefs[Keys.isPaused] = paused
        }
    }

    suspend fun clear() {
        context.activeTimerDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private object Keys {
        val mode = stringPreferencesKey("mode")
        val startedAt = longPreferencesKey("started_at")
        val expectedEnd = longPreferencesKey("expected_end")
        val durationMillis = longPreferencesKey("duration_millis")
        val remainingMillis = longPreferencesKey("remaining_millis")
        val isPaused = booleanPreferencesKey("is_paused")
    }
}
