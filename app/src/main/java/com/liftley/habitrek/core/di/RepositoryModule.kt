package com.liftley.habitrek.core.di

import com.liftley.habitrek.data.repository.CompletionRepositoryImpl
import com.liftley.habitrek.data.repository.HabitRepositoryImpl
import com.liftley.habitrek.domain.repository.CompletionRepository
import com.liftley.habitrek.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindCompletionRepository(impl: CompletionRepositoryImpl): CompletionRepository
}