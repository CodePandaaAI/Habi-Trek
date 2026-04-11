package com.liftley.habitrek.presentation.featureReviewScreen.model

import androidx.compose.ui.graphics.Color
import java.time.YearMonth

data class ReviewUiState(
    // Read Completions of the habit
    val habitCompletions: Set<Long> = emptySet(),

    // Current Year and Month Tracking for month and year navigation and Calendar UI
    val currentYearMonth: YearMonth = YearMonth.now(),

    // Today's Date for adding or removing today's completion as done or for different ui elements
    val todayDate: Long = 0L,

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
    // Real Habit Object from Database
    val habitUiModel: ReviewUiModel = ReviewUiModel(
        id = 0,
        name = "Default",
        color = Color(habitPalette[0]),
        isCompletedToday = false,
        durationMinutes = 0
    )
)