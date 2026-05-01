package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftley.habitrek.domain.usecase.GenerateAiSummaryUseCase
import com.liftley.habitrek.domain.usecase.GetCachedSummaryUseCase
import com.liftley.habitrek.domain.usecase.GetHabitsWithTodayStatusUseCase
import com.liftley.habitrek.domain.usecase.ToggleHabitCompletionUseCase
import com.liftley.habitrek.presentation.featureHomeScreen.model.HomeUiState
import com.liftley.habitrek.presentation.featureHomeScreen.model.toHomeUiModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHabitsWithTodayStatusUseCase: GetHabitsWithTodayStatusUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val generateAiSummaryUseCase: GenerateAiSummaryUseCase,
    private val getCachedSummaryUseCase: GetCachedSummaryUseCase
) : ViewModel() {
    private val _mutableState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _mutableState.asStateFlow()

    private val todayDateMillis =
        LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    private val clickMutex = Mutex()

    init {
        observeHabits()
    }

    private fun observeHabits() {
        viewModelScope.launch {
            try {
                getHabitsWithTodayStatusUseCase().collect {
                    if (it.habits.isEmpty() && it.completedIdSet.isEmpty()) {
                        _mutableState.value = HomeUiState.Empty
                    } else {
                        val habits = it.toHomeUiModelList()
                        
                        // Preserve existing AI summary state if we already have it
                        val currentState = _mutableState.value
                        val existingSummary = (currentState as? HomeUiState.Success)?.aiSummary

                        _mutableState.value = HomeUiState.Success(
                            habits = habits,
                            aiSummary = existingSummary
                        )

                        // Try to fetch today's cached summary from DB
                        if (existingSummary == null) {
                            loadCachedSummary()
                        }
                    }
                }
            } catch (e: Exception) {
                _mutableState.value = HomeUiState.Error(message = "Something Went Wrong")
            }
        }
    }

    /**
     * Loads today's cached summary from the database.
     * No AI engine initialization happens here.
     */
    private fun loadCachedSummary() {
        viewModelScope.launch {
            try {
                val cached = getCachedSummaryUseCase()
                if (cached != null) {
                    val freshState = _mutableState.value
                    if (freshState is HomeUiState.Success) {
                        _mutableState.value = freshState.copy(aiSummary = cached)
                    }
                }
            } catch (_: Exception) {
                // Silently fail — user can still manually generate
            }
        }
    }

    /**
     * Called explicitly by the user via the "Generate Summary" button.
     * This is the ONLY way a summary gets generated — never automatic.
     */
    fun onGenerateSummaryClick() {
        val currentState = _mutableState.value
        if (currentState !is HomeUiState.Success) return

        viewModelScope.launch {
            _mutableState.value = currentState.copy(isAiLoading = true)
            try {
                val result = generateAiSummaryUseCase()
                val freshState = _mutableState.value
                if (freshState is HomeUiState.Success) {
                    _mutableState.value = freshState.copy(
                        aiSummary = result,
                        isAiLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val freshState = _mutableState.value
                if (freshState is HomeUiState.Success) {
                    _mutableState.value = freshState.copy(
                        aiSummary = "Unable to generate summary right now. Please check your internet connection.",
                        isAiLoading = false
                    )
                }
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