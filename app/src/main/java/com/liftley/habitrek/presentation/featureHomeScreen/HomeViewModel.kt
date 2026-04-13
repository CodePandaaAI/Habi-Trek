package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftley.habitrek.domain.usecase.GetHabitsWithTodayStatusUseCase
import com.liftley.habitrek.domain.usecase.ToggleHabitCompletionUseCase
import com.liftley.habitrek.presentation.featureHomeScreen.model.HomeUiState
import com.liftley.habitrek.presentation.featureHomeScreen.model.toHomeUiModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHabitsWithTodayStatusUseCase: GetHabitsWithTodayStatusUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase) : ViewModel() {
    private val _mutableState = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _mutableState.asStateFlow()

    private val todayDateMillis =
        LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    private val clickMutex = Mutex()

    init {
        observeHabits()
    }

    private fun observeHabits() {
        viewModelScope.launch {
            getHabitsWithTodayStatusUseCase().collect { it ->
                val habits = it.toHomeUiModelList()
                _mutableState.update { it.copy(habits = habits) }
            }
        }
    }

    fun toggleHabitCompletion(habitId: Int) {
        viewModelScope.launch {
            clickMutex.withLock {
                toggleHabitCompletionUseCase(habitId, todayDateMillis)
            }
        }
    }
}