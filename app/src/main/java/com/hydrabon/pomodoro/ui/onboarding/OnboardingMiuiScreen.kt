package com.hydrabon.pomodoro.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingMiuiScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = "Keep Pomodoro Focus alive on MIUI", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "1. Disable battery optimizations", style = MaterialTheme.typography.titleMedium)
        Text(text = "MIUI may kill timers aggressively. Allow Pomodoro Focus to ignore battery optimizations.")
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                runCatching { context.startActivity(intent) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Request battery exception")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "2. Enable Autostart", style = MaterialTheme.typography.titleMedium)
        Text(text = "Open MIUI Security → Permissions → Autostart and allow Pomodoro Focus.")
        Button(
            onClick = {
                val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    putExtra("extra_pkgname", context.packageName)
                }
                val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    context.startActivity(fallback)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Open Autostart settings")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "3. Lock in Recents", style = MaterialTheme.typography.titleMedium)
        Text(text = "Swipe down on Pomodoro Focus in the recents menu and tap the lock icon.")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "4. Allow exact alarms & notifications", style = MaterialTheme.typography.titleMedium)
        Text(text = "Ensure exact alarms and notifications stay enabled for reliable end-of-session alerts.")
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Back")
        }
    }
}
