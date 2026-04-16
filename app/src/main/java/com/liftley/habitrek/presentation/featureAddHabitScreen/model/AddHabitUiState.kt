package com.liftley.habitrek.presentation.featureAddHabitScreen.model

import androidx.compose.ui.graphics.Color

data class AddHabitUiState(
    val habitUiModel: AddHabitUiModel =
        AddHabitUiModel(
            id = 0,
            name = "",
            color = Color(0UL),
            durationMinutes = 0
        )
)