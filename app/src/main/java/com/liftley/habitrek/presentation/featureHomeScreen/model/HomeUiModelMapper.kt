package com.liftley.habitrek.presentation.featureHomeScreen.model

import androidx.compose.ui.graphics.Color
import com.liftley.habitrek.domain.model.Habit

fun List<Habit>.toHomeUiModelList(): List<HomeUiModel> {
    return map { habit ->
        HomeUiModel(
            id = habit.id,
            name = habit.name,
            color = Color(habit.color.color),
            isCompletedToday = habit.isCompletedToday,
            durationMinutes = habit.durationMinutes
        )
    }
}