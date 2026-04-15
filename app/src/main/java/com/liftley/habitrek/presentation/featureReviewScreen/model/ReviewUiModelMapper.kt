package com.liftley.habitrek.presentation.featureReviewScreen.model

import androidx.compose.ui.graphics.Color
import com.liftley.habitrek.domain.model.Habit
import com.liftley.habitrek.domain.model.HabitColor

fun Habit.toReviewUiModel(): ReviewUiModel {
    return ReviewUiModel(
        id = id,
        name = name,
        color = Color(color.color),
        isCompletedToday = false,
        durationMinutes = durationMinutes
    )
}

fun ReviewUiModel.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        color = HabitColor(color.value),
        durationMinutes = durationMinutes
    )
}