package com.hydrabon.pomodoro.appwidget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.action.clickable
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.unit.dp
import com.hydrabon.pomodoro.MainActivity

class PomodoroGlanceWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(actionStartActivity(MainActivity::class.java))
        ) {
            Text(text = "Pomodoro Focus")
            Text(text = "Tap to open timer")
        }
    }
}

class PomodoroGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PomodoroGlanceWidget()
}
