package com.hydrabon.pomodoro.data.session

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hydrabon.pomodoro.model.TimerMode

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val mode: TimerMode,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long,
    val completed: Boolean
)
