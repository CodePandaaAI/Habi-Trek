package com.liftley.habitrek.presentation.featureWebSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftley.habitrek.data.repository.SearchRepositoryImpl
import com.liftley.habitrek.presentation.featureWebSearch.model.SearchScreenState
import com.liftley.habitrek.presentation.featureWebSearch.model.toSearchScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepositoryImpl
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
            _mutableState.value = repository.search(query.value).toSearchScreenState()
        }
    }
}
