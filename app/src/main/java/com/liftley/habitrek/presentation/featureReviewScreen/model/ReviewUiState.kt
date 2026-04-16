package com.liftley.habitrek.presentation.featureReviewScreen.model

import androidx.compose.ui.graphics.Color
import java.time.YearMonth

sealed interface ReviewUiState {
    data object Loading : ReviewUiState
    data class Success(
        // Read Completions of the habit
        val habitCompletions: Set<Long> = emptySet(),

        // Real Habit Object from Database
        val reviewUiModel: ReviewUiModel = ReviewUiModel(
            id = 0,
            name = "Default",
            color = Color(0UL),
            isCompletedToday = false,
            durationMinutes = 0
        ),

        // Current Year and Month Tracking for Calendar UI
        val currentYearMonth: YearMonth = YearMonth.now(),

        /*
        State Variables for Delete Dialog states like isVisible and its textField which requires
        the user to write exactly "delete" word though it can be UPPERCASE or Lower Case as we at the
        end we lower case entire string so it should be just delete with any format.
        */
        val isDeleteHabitDialogVisible: Boolean = false,
        val deleteHabitDialogText: String = ""
    ) : ReviewUiState

    data class Error(val message: String) : ReviewUiState
}