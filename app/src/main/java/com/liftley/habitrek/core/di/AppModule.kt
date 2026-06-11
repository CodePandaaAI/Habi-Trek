package com.liftley.habitrek.core.di

import android.content.Context
import androidx.room.Room
import com.liftley.habitrek.data.local.dao.AiSummaryDao
import com.liftley.habitrek.data.local.dao.CompletionDao
import com.liftley.habitrek.data.local.dao.HabitDao
import com.liftley.habitrek.data.local.database.HabitTrackerAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitTrackerAppDatabase {
        return Room.databaseBuilder(
                context, HabitTrackerAppDatabase::class.java, "habit_tracker_database"
            ).build()
    }

    @Provides
    @Singleton
    fun providesHabitsDao(database: HabitTrackerAppDatabase): HabitDao = database.habitDao()

    @Provides
    @Singleton
    fun providesCompletionDao(database: HabitTrackerAppDatabase): CompletionDao = database.completionDao()

    @Provides
    @Singleton
    fun providesAiSummaryDao(database: HabitTrackerAppDatabase): AiSummaryDao = database.aiSummaryDao()
}