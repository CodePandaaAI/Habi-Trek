package com.liftley.habitrek.presentation.featureAddHabitScreen.model

import androidx.compose.ui.graphics.Color

data class AddHabitUiState(
    val habitPalette: List<Long> = listOf(
        0L,
        0xFFE57373,
        0xFFF06292,
        0xFFBA68C8,
        0xFF9575CD,
        0xFF7986CB,
        0xFF64B5F6,
        0xFF4FC3F7,
        0xFF4DD0E1,
        0xFF4DB6AC,
        0xFF81C784,
        0xFFAED581,
        0xFFDCE775,
        0xFFFFD54F,
        0xFFFFB74D
    ),
    val habitUiModel: AddHabitUiModel = AddHabitUiModel(
        color = Color(habitPalette[0])
    )
)