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
import java.time.YearMonth
import java.time.ZoneOffset

@HiltViewModel(assistedFactory = ReviewViewModel.Factory::class)
class ReviewViewModel @AssistedInject constructor(
    @Assisted val habitId: Int,
    private val habitRepository: HabitRepository,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val getHabitCompletionsUseCase: GetHabitCompletionsUseCase
) : ViewModel() {

    companion object {
        val habitPalette: List<ULong> = listOf(
            0UL,
            Color(0xFFE57373).value,
            Color(0xFFF06292).value,
            Color(0xFFBA68C8).value,
            Color(0xFF9575CD).value,
            Color(0xFF7986CB).value,
            Color(0xFF64B5F6).value,
            Color(0xFF4FC3F7).value,
            Color(0xFF4DD0E1).value,
            Color(0xFF4DB6AC).value,
            Color(0xFF81C784).value,
            Color(0xFFAED581).value,
            Color(0xFFDCE775).value,
            Color(0xFFFFD54F).value,
            Color(0xFFFFB74D).value
        )
    }

    private val _mutableState = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _mutableState.asStateFlow()


    private var nameUpdateJob: Job? = null

    private var colorUpdateJob: Job? = null

    private var dateUpdateJob: Job? = null

    private val clickMutex = Mutex()

    // Current Year and Month Tracking for month and year navigation and Calendar UI
    var currentYearMonth: YearMonth = YearMonth.now()
        private set

    // Today's Date as per UTC
    val todayDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    // Initial Data Loading
    init {
        viewModelScope.launch {
            val habit = habitRepository.getHabitWithId(habitId)
            _mutableState.update { currentState ->
                currentState.copy(
                    habitUiModel = habit.toReviewUiModel()
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


    fun changeYearMonth(monthsToAdd: Long) {
        currentYearMonth.plusMonths(monthsToAdd)
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

    fun updateNewHabitDuration(newValue: String) {
        val value = newValue.filter { it.isDigit() }.toIntOrNull() ?: 0
        val durationMinutes = if (value !in 0..1440) 0 else value

        _mutableState.update { currentState ->
            currentState.copy(
                habitUiModel = currentState.habitUiModel.copy(durationMinutes = durationMinutes)
            )
        }

        if (durationMinutes == 0) return
        dateUpdateJob?.cancel()
        dateUpdateJob = viewModelScope.launch {
            delay(700)
            habitRepository.upsertHabit(
                state.value.habitUiModel.toDomain()
            )
        }
    }

    fun updateHabitColor(index: Int) {
        _mutableState.update { currentState ->
            val color = habitPalette[index]
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

    fun currentYearMonthName(): String = "${
        currentYearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
    } ${currentYearMonth.year}"

    // Factory provides hilt the habitId parameter which is a runtime value
    @AssistedFactory
    interface Factory {
        fun create(habitId: Int): ReviewViewModel
    }
}