package com.liftley.habitrek.domain.model

data class Habit(
    val id: Int = 0,
    val name: String,
    val color: HabitColor = HabitColor(0L),
    val isCompletedToday: Boolean,
    val durationMinutes: Int = 0
)