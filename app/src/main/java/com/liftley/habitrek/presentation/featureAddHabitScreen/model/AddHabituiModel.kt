package com.liftley.habitrek.presentation.featureAddHabitScreen.model

import androidx.compose.ui.graphics.Color

data class AddHabitUiModel(
    val id: Int = 0,
    val name: String = "",
    val color: Color,
    val durationMinutes: Int = 0
)