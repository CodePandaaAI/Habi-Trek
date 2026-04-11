package com.liftley.habitrek.presentation.featureReviewScreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftley.habitrek.domain.repository.HabitRepository
import com.liftley.habitrek.domain.usecase.GetHabitCompletionsUseCase
import com.liftley.habitrek.domain.usecase.ToggleHabitCompletionUseCase
import com.liftley.habitrek.presentation.featureReviewScreen.model.ReviewUiState
import com.liftley.habitrek.presentation.featureReviewScreen.model.toDomain
import com.liftley.habitrek.presentation.featureReviewScreen.model.toReviewUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.ZoneOffset

@HiltViewModel(assistedFactory = ReviewViewModel.Factory::class)
class ReviewViewModel @AssistedInject constructor(
    @Assisted val habitId: Int,
    private val habitRepository: HabitRepository,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val getHabitCompletionsUseCase: GetHabitCompletionsUseCase
) : ViewModel() {
    private val _mutableState = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _mutableState.asStateFlow()

    val todayDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    private var nameUpdateJob: Job? = null

    private var colorUpdateJob: Job? = null

    private var dateUpdateJob: Job? = null

    private val clickMutex = Mutex()

    // Initial Data Loading

    init {
        viewModelScope.launch {
            val habit = habitRepository.getHabitWithId(habitId)
            _mutableState.update { currentState ->
                currentState.copy(
                    habitUiModel = habit.toReviewUiModel(),
                    todayDate = todayDate
                )
            }
        }
        startObservingHabitCompletions()
    }

    /*
    Flow that observes all habit completions till date and continuously Updates UI(To Keep it accurate)
    */

    fun startObservingHabitCompletions() {
        viewModelScope.launch {
            // Actively listen (collect) to database changes
            getHabitCompletionsUseCase(habitId).collect { habitStatus ->
                _mutableState.update { currentState ->
                    currentState.copy(
                        habitCompletions = habitStatus.completedTimestamps,
                        habitUiModel = currentState.habitUiModel.copy(
                            isCompletedToday = habitStatus.isCompletedToday
                        )
                    )
                }
            }
        }
    }


    fun changeMonth(monthsToAdd: Long) {
        _mutableState.update { currentState ->
            val newYearMonth = currentState.currentYearMonth.plusMonths(monthsToAdd)
            currentState.copy(currentYearMonth = newYearMonth)
        }
    }

    // Updating Habit Data

    fun updateHabitName(name: String) {
        _mutableState.update { currentState ->
            currentState.copy(
                habitUiModel = currentState.habitUiModel.copy(name = name)
            )
        }

        // Cancel the previous job before starting a new one
        nameUpdateJob?.cancel()
        nameUpdateJob = viewModelScope.launch {
            delay(700) // Debounce for 700ms
            habitRepository.upsertHabit(
                state.value.habitUiModel.toDomain()
            )
        }
    }

    fun updateNewHabitDuration(duration: Int) {
        val time = if (duration > 1440) 0 else duration
        _mutableState.update { currentState ->
            currentState.copy(
                habitUiModel = currentState.habitUiModel.copy(durationMinutes = time)
            )
        }

        if (time == 0) return
        dateUpdateJob?.cancel()
        dateUpdateJob = viewModelScope.launch {
            delay(700)
            habitRepository.upsertHabit(
                state.value.habitUiModel.toDomain()
            )
        }
    }

    fun updateNewHabitColor(index: Int) {
        _mutableState.update { currentState ->
            val color = state.value.habitPalette[index]
            currentState.copy(
                habitUiModel = currentState.habitUiModel.copy(color = Color(color))
            )
        }
        colorUpdateJob?.cancel()
        colorUpdateJob = viewModelScope.launch {
            delay(700)
            habitRepository.upsertHabit(
                state.value.habitUiModel.toDomain()
            )
        }
    }

    // Adding or Removing Completions(Marking completed days and removing not completed ones)
    fun toggleHabitCompletion(dateMillis: Long) {
        viewModelScope.launch {
            clickMutex.withLock {
                toggleHabitCompletionUseCase(habitId, dateMillis)
            }
        }
    }

    // Delete Habit Entity with All Completions Permanently
    fun deleteHabit() {
        viewModelScope.launch {
            val habitId = state.value.habitUiModel.id
            habitRepository.deleteHabit(habitId)
        }
    }

    // Factory provides hilt the habitId parameter which is a runtime value
    @AssistedFactory
    interface Factory {
        fun create(habitId: Int): ReviewViewModel
    }
}