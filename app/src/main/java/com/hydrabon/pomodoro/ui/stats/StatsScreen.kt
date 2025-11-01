package com.hydrabon.pomodoro.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hydrabon.pomodoro.model.DailyStats
import java.time.format.DateTimeFormatter

@Composable
fun StatsScreen(state: StatsUiState) {
    val formatter = DateTimeFormatter.ofPattern("EEE dd MMM")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.dailyStats) { stats ->
            DailyStatsCard(stats, formatter)
        }
    }
}

@Composable
private fun DailyStatsCard(stats: DailyStats, formatter: DateTimeFormatter) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = formatter.format(stats.date),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Completed sessions: ${stats.completedFocusSessions}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Focus minutes: ${stats.totalFocusDurationMinutes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
