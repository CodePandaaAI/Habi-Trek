package com.liftley.habitrek.data.repository

import com.liftley.habitrek.data.local.dao.HabitDao
import com.liftley.habitrek.data.local.entity.toDomainHabitFlow
import com.liftley.habitrek.data.local.entity.toEntity
import com.liftley.habitrek.data.local.entity.toFlowListHabit
import com.liftley.habitrek.domain.model.Habit
import com.liftley.habitrek.domain.repository.HabitRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class HabitRepositoryImpl @Inject constructor(private val habitDao: HabitDao): HabitRepository {

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().toFlowListHabit()
    }

    override suspend fun upsertHabit(habit: Habit) {
        habitDao.upsertHabit(habit.toEntity())
    }


    override suspend fun deleteHabit(habitId: Int) {
        habitDao.deleteHabit(habitId)
    }

    override suspend fun getHabitWithId(id: Int): Flow<Habit> {
        return habitDao.getHabitWithId(id).toDomainHabitFlow()
    }
}