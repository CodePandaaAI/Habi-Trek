package com.liftley.habitrek.data.local.entity

import com.liftley.habitrek.domain.model.Habit
import com.liftley.habitrek.domain.model.HabitColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        color = color.color.toLong(),
        durationMinutes = durationMinutes,
    )
}

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        color = HabitColor(color.toULong()),
        isCompletedToday = false,
        durationMinutes = durationMinutes,
    )
}

fun Flow<List<HabitEntity>>.toFlowListHabit(): Flow<List<Habit>> {
    return this.map { list ->
        list.map { entity ->
            Habit(
                id = entity.id,
                name = entity.name,
                color = HabitColor(entity.color.toULong()),
                isCompletedToday = false,
                durationMinutes = entity.durationMinutes
            )
        }
    }
}