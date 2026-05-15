package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.model.Completion
import com.liftley.habitrek.domain.repository.CompletionRepository
import jakarta.inject.Inject

class ToggleHabitCompletionUseCase @Inject constructor(private val completionRepository: CompletionRepository) {
    suspend operator fun invoke(habitId: Int, dateMillis: Long) {
        if (completionRepository.checkIfCompletionExistsWithHabitIdAndDate(
                habitId,
                dateMillis
            )
        ) {
            completionRepository.deleteCompletionWithIdAndDate(
                habitId = habitId,
                dateMillis = dateMillis
            )
        } else {
            completionRepository.addCompletion(
                Completion(
                    id = 0,
                    habitId = habitId,
                    dateMillis = dateMillis
                )
            )
        }
    }
}