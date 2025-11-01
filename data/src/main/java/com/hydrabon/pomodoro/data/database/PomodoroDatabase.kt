package com.hydrabon.pomodoro.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hydrabon.pomodoro.data.session.SessionDao
import com.hydrabon.pomodoro.data.session.SessionEntity

@Database(
    entities = [SessionEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(SessionConverters::class)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    companion object {
        const val DATABASE_NAME = "pomodoro.db"
    }
}
