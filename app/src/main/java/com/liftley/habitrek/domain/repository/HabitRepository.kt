package com.liftley.habitrek.domain.repository

import com.liftley.habitrek.domain.model.Habit

import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    fun getAllHabits(): Flow<List<Habit>>

    suspend fun upsertHabit(habit: Habit)


    suspend fun deleteHabit(habitId: Int)

    suspend fun getHabitWithId(id: Int): Flow<Habit>
}