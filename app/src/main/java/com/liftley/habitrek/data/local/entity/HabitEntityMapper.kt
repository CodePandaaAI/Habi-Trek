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
        durationMinutes = durationMinutes,
    )
}

fun Flow<HabitEntity>.toDomainHabitFlow(): Flow<Habit> {
    return this.map { habitEntity ->
        Habit(
            id = habitEntity.id,
            name = habitEntity.name,
            color = HabitColor(habitEntity.color.toULong()),
            durationMinutes = habitEntity.durationMinutes,
        )
    }
}

fun Flow<List<HabitEntity>>.toFlowListHabit(): Flow<List<Habit>> {
    return this.map { list ->
        list.map { entity ->
            Habit(
                id = entity.id,
                name = entity.name,
                color = HabitColor(entity.color.toULong()),
                durationMinutes = entity.durationMinutes
            )
        }
    }
}