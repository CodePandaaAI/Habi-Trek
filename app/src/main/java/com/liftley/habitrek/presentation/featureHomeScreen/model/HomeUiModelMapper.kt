package com.liftley.habitrek.presentation.featureHomeScreen.model

import androidx.compose.ui.graphics.Color
import com.liftley.habitrek.domain.model.Habit

fun Habit.toHomeUiModel(): HomeUiModel {
    return HomeUiModel(
        id = id,
        name = name,
        color = Color(color.color),
        isCompletedToday = isCompletedToday,
        durationMinutes = durationMinutes
    )
}

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