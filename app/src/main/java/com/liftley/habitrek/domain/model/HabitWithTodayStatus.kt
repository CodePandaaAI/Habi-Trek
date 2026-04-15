package com.liftley.habitrek.domain.model

data class HabitWithTodayStatus(
    val completedTimestamps: Set<Long>,
    val isCompletedToday: Boolean
)