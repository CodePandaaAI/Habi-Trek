package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.model.Habit
import com.liftley.habitrek.domain.repository.HabitRepository
import jakarta.inject.Inject

class CreateHabitUseCase @Inject constructor(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habit: Habit) {
        habitRepository.upsertHabit(habit)
    }
}