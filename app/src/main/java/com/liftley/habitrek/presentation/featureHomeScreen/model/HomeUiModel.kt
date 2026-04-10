package com.liftley.habitrek.presentation.featureHomeScreen.model

import androidx.compose.ui.graphics.Color

data class HomeUiModel(
    val id: Int,
    val name: String,
    val color: Color,
    val isCompletedToday: Boolean,
    val durationMinutes: Int
)