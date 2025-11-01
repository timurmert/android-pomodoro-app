package com.hydrabon.pomodoro.data.di

import android.content.Context
import androidx.room.Room
import com.hydrabon.pomodoro.data.database.PomodoroDatabase
import com.hydrabon.pomodoro.data.session.SessionDao
import com.hydrabon.pomodoro.data.session.SessionRepositoryImpl
import com.hydrabon.pomodoro.data.settings.SettingsDataSource
import com.hydrabon.pomodoro.data.settings.SettingsRepositoryImpl
import com.hydrabon.pomodoro.repository.SessionRepository
import com.hydrabon.pomodoro.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PomodoroDatabase =
        Room.databaseBuilder(context, PomodoroDatabase::class.java, PomodoroDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSessionDao(database: PomodoroDatabase): SessionDao = database.sessionDao()

    @Provides
    @Singleton
    fun provideSessionRepository(sessionDao: SessionDao): SessionRepository =
        SessionRepositoryImpl(sessionDao)

    @Provides
    @Singleton
    fun provideSettingsDataSource(@ApplicationContext context: Context): SettingsDataSource =
        SettingsDataSource(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(dataSource: SettingsDataSource): SettingsRepository =
        SettingsRepositoryImpl(dataSource)
}
