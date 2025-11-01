package com.hydrabon.pomodoro.timer.recovery

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hydrabon.pomodoro.timer.ActiveTimerStorage
import com.hydrabon.pomodoro.timer.service.TimerService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class TimerRecoveryWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val activeTimerStorage: ActiveTimerStorage
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val stored = activeTimerStorage.activeTimer.firstOrNull() ?: return Result.success()
        val intent = TimerService.intent(
            appContext,
            stored.mode,
            stored.remainingMillis,
            stored.expectedEndAt.toEpochMilli()
        ).apply { action = TimerService.ACTION_RESUME }
        ContextCompat.startForegroundService(appContext, intent)
        return Result.success()
    }

    companion object {
        private const val UNIQUE_NAME = "timer_recovery"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<TimerRecoveryWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
