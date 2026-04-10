package com.liftley.habitrek.core.di

import android.content.Context
import androidx.room.Room
import com.liftley.habitrek.data.local.dao.AddHabitDao
import com.liftley.habitrek.data.local.dao.HabitsDao
import com.liftley.habitrek.data.local.dao.ReviewHabitDao
import com.liftley.habitrek.data.local.database.HabitTrackerAppDatabase
import com.liftley.habitrek.data.remote.api.SearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    fun providesHabitsDao(database: HabitTrackerAppDatabase): HabitsDao = database.habitsDao()

    @Provides
    @Singleton
    fun providesAddHabitDao(database: HabitTrackerAppDatabase): AddHabitDao = database.addHabitDao()

    @Provides
    @Singleton
    fun providesReviewHabitDao(database: HabitTrackerAppDatabase): ReviewHabitDao = database.reviewHabitDao()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSearchApi(retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }
}