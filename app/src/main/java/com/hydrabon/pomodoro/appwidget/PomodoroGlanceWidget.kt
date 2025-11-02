package com.hydrabon.pomodoro.appwidget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.compose.ui.unit.dp
import com.hydrabon.pomodoro.MainActivity

class PomodoroGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content() }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java)))
        ) {
            Text(text = "Pomodoro Focus")
            Text(text = "Tap to open timer")
        }
    }
}

class PomodoroGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PomodoroGlanceWidget()
}
