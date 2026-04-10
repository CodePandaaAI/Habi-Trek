package com.liftley.habitrek.data.repository

import com.liftley.habitrek.data.local.dao.CompletionDao
import com.liftley.habitrek.data.local.entity.toFlowCompletionList
import com.liftley.habitrek.data.local.entity.toEntity
import com.liftley.habitrek.domain.model.Completion
import com.liftley.habitrek.domain.repository.CompletionRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CompletionRepositoryImpl @Inject constructor(private val completionDao: CompletionDao) :
    CompletionRepository {

    override fun getIdOfAllHabitsCompletedForDate(dateMillis: Long): Flow<List<Int>> {
        return completionDao.getIdOfAllHabitsCompletedForDate(dateMillis)
    }

    override suspend fun addCompletion(completion: Completion) {
        completionDao.addCompletion(completion.toEntity())
    }

    override suspend fun checkIfCompletionExistsWithHabitIdAndDate(
        habitId: Int,
        dateMillis: Long
    ): Boolean {
        return completionDao.checkIfCompletionExistsWithHabitIdAndDate(habitId, dateMillis)
    }

    override fun getAllCompletionsForHabitWithId(habitId: Int): Flow<List<Completion>> {
        return completionDao.getAllCompletionsForHabitWithId(habitId = habitId).toFlowCompletionList()
    }

    override suspend fun deleteCompletionWithIdAndDate(habitId: Int, dateMillis: Long) {
        completionDao.deleteCompletionWithIdAndDate(habitId, dateMillis)
    }
}