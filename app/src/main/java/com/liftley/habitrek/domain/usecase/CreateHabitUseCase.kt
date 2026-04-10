package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.model.Habit
import com.liftley.habitrek.domain.model.HabitColor
import com.liftley.habitrek.domain.repository.HabitRepository
import jakarta.inject.Inject

class CreateHabitUseCase @Inject constructor(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habitName: String, habitColor: Long, durationMinutes: Int) {
        if (habitName.isEmpty() || durationMinutes <= 0) return

        habitRepository.upsertHabit(
            Habit(
                id = 0,
                name = habitName,
                color = HabitColor(habitColor),
                durationMinutes = durationMinutes,
                isCompletedToday = false
            )
        )
    }
}