package com.hydrabon.pomodoro.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SessionEntity): Long

    @Query("SELECT * FROM sessions WHERE startedAtEpochMillis BETWEEN :from AND :to ORDER BY startedAtEpochMillis ASC")
    fun observeSessionsBetween(from: Long, to: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE startedAtEpochMillis >= :from ORDER BY startedAtEpochMillis ASC")
    fun observeSessionsFrom(from: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE startedAtEpochMillis BETWEEN :from AND :to ORDER BY startedAtEpochMillis ASC")
    suspend fun getSessionsBetween(from: Long, to: Long): List<SessionEntity>
}
