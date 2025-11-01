package com.hydrabon.pomodoro.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hydrabon.pomodoro.R
import com.hydrabon.pomodoro.model.TimerMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerNotificationFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun build(mode: TimerMode, remainingMillis: Long, isPaused: Boolean): Notification {
        ensureChannel()
        val minutes = remainingMillis / 60000
        val seconds = (remainingMillis / 1000) % 60
        val formatted = String.format("%02d:%02d", minutes, seconds)
        val titleRes = when (mode) {
            TimerMode.FOCUS -> R.string.notification_focus
            TimerMode.SHORT_BREAK -> R.string.notification_short_break
            TimerMode.LONG_BREAK -> R.string.notification_long_break
        }
        val statusText = if (isPaused) {
            context.getString(R.string.notification_status_paused, formatted)
        } else {
            context.getString(R.string.notification_status_running, formatted)
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_timer)
            .setContentTitle(context.getString(titleRes))
            .setContentText(statusText)
            .setOngoing(!isPaused)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(statusText))
            .build()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = NotificationManagerCompat.from(context)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "timer"
        const val NOTIFICATION_ID = 1001
    }
}
