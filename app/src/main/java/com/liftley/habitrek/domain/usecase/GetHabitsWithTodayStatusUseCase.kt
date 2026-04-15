package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.model.HabitListWithTodayStatusList
import com.liftley.habitrek.domain.repository.CompletionRepository
import com.liftley.habitrek.domain.repository.HabitRepository
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@Singleton
class GetHabitsWithTodayStatusUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository
) {
    private val todayDateMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    operator fun invoke(): Flow<HabitListWithTodayStatusList> {
        return combine(
            flow = habitRepository.getAllHabits(),
            flow2 = completionRepository.getIdOfAllHabitsCompletedForDate(todayDateMillis)
        ) { habits, habitIds ->
            val completedIdSet = habitIds.toSet()

            HabitListWithTodayStatusList(habits = habits, completedIdSet)
        }
    }
}