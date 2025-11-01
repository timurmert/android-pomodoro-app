package com.hydrabon.pomodoro.timer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.hydrabon.pomodoro.timer.ActiveTimerStorage
import com.hydrabon.pomodoro.timer.service.TimerService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var activeTimerStorage: ActiveTimerStorage

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_ALARM_EXPIRED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            val stored = activeTimerStorage.activeTimer.firstOrNull()
            if (stored != null) {
                val serviceIntent = TimerService.intent(
                    context,
                    stored.mode,
                    stored.remainingMillis,
                    stored.expectedEndAt.toEpochMilli()
                ).apply { action = TimerService.ACTION_RESUME }
                ContextCompat.startForegroundService(context, serviceIntent)
            }
            pendingResult.finish()
        }
    }

    companion object {
        const val ACTION_ALARM_EXPIRED = "com.hydrabon.pomodoro.action.ALARM_EXPIRED"
        const val EXTRA_MODE = "extra_mode"
    }
}
