package com.liftley.habitrek.presentation.featureWebSearch.model

import com.liftley.habitrek.domain.model.SearchArticle

sealed interface SearchScreenState {
    data class Success(val articles: List<SearchArticle> = emptyList()) : SearchScreenState
    data class Error(val message: String) : SearchScreenState
    data object Idle : SearchScreenState
    data object Loading : SearchScreenState
}
