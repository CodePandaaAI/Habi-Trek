package com.liftley.habitrek.presentation.featureWebSearch.model

sealed interface SearchScreenState {
    data class Success(val searchResult: List<ResultItem> = emptyList()) : SearchScreenState
    data class Error(val error: String? = null) : SearchScreenState

    data object Idle: SearchScreenState

    data object Loading: SearchScreenState
}

data class SearchResult(
    val results: List<ResultItem> = emptyList(),
    val error: String? = null
)

data class ResultItem(
    val title: String,
    val link: String,
    val snippet: String
)

fun SearchResult.toSearchScreenState(): SearchScreenState {
    return if(this.error == null) {
        SearchScreenState.Success(searchResult = this.results)
    } else SearchScreenState.Error(error = this.error)
}