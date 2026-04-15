package com.liftley.habitrek.presentation.featureHomeScreen.model



sealed interface HomeUiState {
    data object Loading: HomeUiState
    data class Success(val habits: List<HomeUiModel>): HomeUiState
    data class Error(val message: String): HomeUiState
}