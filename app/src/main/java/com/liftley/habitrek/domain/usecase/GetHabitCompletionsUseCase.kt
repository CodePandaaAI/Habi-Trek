package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.repository.CompletionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneOffset

class GetHabitCompletionsUseCase @Inject constructor(private val completionRepository: CompletionRepository) {
    private val todayDateMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    operator fun invoke(habitId: Int): Flow<HabitStatus> {
        return completionRepository.getAllCompletionsForHabitWithId(habitId).map { completions ->
            val timestamps = completions.map { it.dateMillis }.toSet()
            val today = todayDateMillis

            HabitStatus(
                completedTimestamps = timestamps,
                isCompletedToday = timestamps.contains(today)
            )
        }
    }
}

data class HabitStatus(
    val completedTimestamps: Set<Long>,
    val isCompletedToday: Boolean
)
