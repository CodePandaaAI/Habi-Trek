package com.liftley.habitrek.presentation.featureHomeScreen.model

sealed interface HomeUiState {
    data object Loading: HomeUiState

    data object Empty: HomeUiState
    
    data class Success(
        val habits: List<HomeUiModel>,
        val aiSummary: String? = null,
        val isAiLoading: Boolean = false,
        val isModelDownloaded: Boolean = false,
        val isDownloading: Boolean = false,
        val downloadProgress: Int = 0,
        val downloadError: String? = null
    ): HomeUiState
    
    data class Error(val message: String): HomeUiState
}