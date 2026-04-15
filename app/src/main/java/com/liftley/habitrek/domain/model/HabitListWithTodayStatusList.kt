package com.liftley.habitrek.domain.model

data class HabitListWithTodayStatusList(
    val habits: List<Habit>,
    val completedIdSet: Set<Int>
)