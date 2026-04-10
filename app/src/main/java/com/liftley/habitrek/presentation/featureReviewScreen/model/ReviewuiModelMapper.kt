package com.liftley.habitrek.presentation.featureReviewScreen.model

import androidx.compose.ui.graphics.Color
import com.liftley.habitrek.domain.model.Habit
import com.liftley.habitrek.domain.model.HabitColor
import com.liftley.habitrek.presentation.featureHomeScreen.model.HomeUiModel

fun Habit.toReviewUiModel(): ReviewUiModel {
    return ReviewUiModel(
        id = id,
        name = name,
        color = Color(color.color),
        isCompletedToday = isCompletedToday,
        durationMinutes = durationMinutes
    )
}

fun List<Habit>.toReviewUiModelList(): List<ReviewUiModel> {
    return map { habit ->
        ReviewUiModel(
            id = habit.id,
            name = habit.name,
            color = Color(habit.color.color),
            isCompletedToday = habit.isCompletedToday,
            durationMinutes = habit.durationMinutes
        )
    }
}

fun ReviewUiModel.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        color = HabitColor(color.value.toLong()),
        isCompletedToday = isCompletedToday,
        durationMinutes = durationMinutes
    )
}