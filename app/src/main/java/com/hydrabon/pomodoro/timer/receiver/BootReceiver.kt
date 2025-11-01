package com.hydrabon.pomodoro.timer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hydrabon.pomodoro.timer.recovery.TimerRecoveryWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            TimerRecoveryWorker.enqueue(context.applicationContext)
        }
    }
}
