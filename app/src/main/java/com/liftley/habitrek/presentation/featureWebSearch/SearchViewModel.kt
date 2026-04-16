package com.liftley.habitrek.presentation.featureWebSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftley.habitrek.domain.repository.SearchRepository
import com.liftley.habitrek.presentation.featureWebSearch.model.SearchScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _mutableState: MutableStateFlow<SearchScreenState> =
        MutableStateFlow(SearchScreenState.Idle)
    val state: StateFlow<SearchScreenState> = _mutableState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    val query = _queryFlow.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _queryFlow.value = newQuery
    }

    fun search() {
        if (query.value.isBlank()) return
        _queryFlow.value = query.value.trim()
        _mutableState.value = SearchScreenState.Loading

        viewModelScope.launch {
            try {
                val articles = repository.search(query.value)
                _mutableState.value = SearchScreenState.Success(articles)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _mutableState.value = SearchScreenState.Error(
                    message = when {
                        e.message?.contains("Unable to resolve host") == true -> "No internet connection."
                        e.message?.contains("timeout") == true -> "Request timed out."
                        else -> "Search failed. Try again."
                    }
                )
            }
        }
    }
}
