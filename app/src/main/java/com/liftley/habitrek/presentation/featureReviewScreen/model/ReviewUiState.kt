package com.liftley.habitrek.presentation.featureReviewScreen.model

import androidx.compose.ui.graphics.Color
import java.time.YearMonth

data class ReviewUiState(
    // Read Completions of the habit
    val habitCompletions: Set<Long> = emptySet(),

    // Real Habit Object from Database
    val habitUiModel: ReviewUiModel = ReviewUiModel(
        id = 0,
        name = "Default",
        color = Color(0UL),
        isCompletedToday = false,
        durationMinutes = 0
    )
)