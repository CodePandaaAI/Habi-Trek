package com.liftley.habitrek.domain.repository

import com.liftley.habitrek.domain.model.Completion
import kotlinx.coroutines.flow.Flow

interface CompletionRepository {

    fun getIdOfAllHabitsCompletedForDate(dateMillis: Long): Flow<List<Int>>

    suspend fun addCompletion(completion: Completion)

    suspend fun checkIfCompletionExistsWithHabitIdAndDate(habitId: Int, dateMillis: Long): Boolean

    fun getAllCompletionsForHabitWithId(habitId: Int): Flow<List<Completion>>

    suspend fun deleteCompletionWithIdAndDate(habitId: Int, dateMillis: Long)
}