package com.liftley.habitrek.domain.model

data class HabitStatus(
    val completedTimestamps: Set<Long>,
    val isCompletedToday: Boolean
)