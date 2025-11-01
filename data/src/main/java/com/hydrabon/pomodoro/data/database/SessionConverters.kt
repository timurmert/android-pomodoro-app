package com.hydrabon.pomodoro.data.database

import androidx.room.TypeConverter
import com.hydrabon.pomodoro.model.TimerMode
import java.time.Instant

class SessionConverters {
    @TypeConverter
    fun fromMode(mode: TimerMode): String = mode.name

    @TypeConverter
    fun toMode(value: String): TimerMode = TimerMode.valueOf(value)

    @TypeConverter
    fun fromInstant(instant: Instant): Long = instant.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long): Instant = Instant.ofEpochMilli(value)
}
