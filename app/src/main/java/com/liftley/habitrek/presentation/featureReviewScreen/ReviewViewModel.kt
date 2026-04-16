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

    private val _mutableState: MutableStateFlow<ReviewUiState> =
        MutableStateFlow(ReviewUiState.Loading)
    val state: StateFlow<ReviewUiState> = _mutableState.asStateFlow()

    private var nameUpdateJob: Job? = null

    private var colorUpdateJob: Job? = null

    private var dateUpdateJob: Job? = null

    private val clickMutex = Mutex()

    // Today's Date as per UTC
    val todayDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    // Initial Data Loading
    init {
        viewModelScope.launch {
            try {
                habitRepository.getHabitWithId(habitId).collect { habit ->
                    _mutableState.value =
                        ReviewUiState.Success(reviewUiModel = habit.toReviewUiModel())
                }
            } catch (e: Exception) {
                _mutableState.value = ReviewUiState.Error(message = "Habit No Longer Exists")
            }
        }
        startObservingHabitCompletions()
    }

    fun startObservingHabitCompletions() {
        viewModelScope.launch {
            getHabitCompletionsUseCase(habitId).collect { habitStatus ->
                _mutableState.update { currentState ->
                    if (currentState is ReviewUiState.Success) {
                        currentState.copy(
                            habitCompletions = habitStatus.completedTimestamps,
                            reviewUiModel = currentState.reviewUiModel.copy(
                                isCompletedToday = habitStatus.isCompletedToday
                            )
                        )
                    } else {
                        currentState
                    }
                }
            }
        }
    }


    fun changeYearMonth(monthsToAdd: Long) {
        _mutableState.update { currentState ->
            if (currentState is ReviewUiState.Success) {
                // plusMonths returns a NEW instance that we now save to Trigger UI modification
                currentState.copy(
                    currentYearMonth = currentState.currentYearMonth.plusMonths(monthsToAdd)
                )
            } else currentState
        }
    }

    // Updating Habit Data
    fun updateHabitName(name: String) {
        _mutableState.update { currentState ->
            if (currentState is ReviewUiState.Success) {
                currentState.copy(reviewUiModel = currentState.reviewUiModel.copy(name = name))
            } else currentState
        }

        val currentState = state.value
        if (currentState is ReviewUiState.Success) {
            val domainHabit = currentState.reviewUiModel.toDomain()
            nameUpdateJob?.cancel()
            nameUpdateJob = viewModelScope.launch {
                delay(700)
                habitRepository.upsertHabit(domainHabit)
            }
        }
    }

    fun updateNewHabitDuration(newValue: String) {
        val value = newValue.filter { it.isDigit() }.toIntOrNull() ?: 0
        val durationMinutes = if (value !in 0..1440) 0 else value

        _mutableState.update { currentState ->
            if (currentState is ReviewUiState.Success) {
                currentState.copy(reviewUiModel = currentState.reviewUiModel.copy(durationMinutes = durationMinutes))
            } else currentState
        }

        if (durationMinutes == 0) return
        val currentState = state.value
        if (currentState is ReviewUiState.Success) {
            val domainHabit = currentState.reviewUiModel.toDomain()
            dateUpdateJob?.cancel()
            dateUpdateJob = viewModelScope.launch {
                delay(700)
                habitRepository.upsertHabit(domainHabit)
            }
        }
    }


    fun updateHabitColor(index: Int) {
        _mutableState.update { currentState ->
            if (currentState is ReviewUiState.Success) {
                currentState.copy(
                    reviewUiModel = currentState.reviewUiModel.copy(
                        color = Color(
                            habitPalette[index]
                        )
                    )
                )
            } else currentState
        }

        val currentState = state.value
        if (currentState is ReviewUiState.Success) {
            val domainHabit = currentState.reviewUiModel.toDomain()
            colorUpdateJob?.cancel()
            colorUpdateJob = viewModelScope.launch {
                delay(700)
                habitRepository.upsertHabit(domainHabit)
            }
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
        if (state.value is ReviewUiState.Success) {
            viewModelScope.launch {
                val habitId = (state.value as ReviewUiState.Success).reviewUiModel.id
                habitRepository.deleteHabit(habitId)
            }
        }
    }

    fun toggleDeleteDialog(toggleValue: Boolean) {
        _mutableState.update { currentState ->
            if (currentState is ReviewUiState.Success) {
                currentState.copy(isDeleteHabitDialogVisible = toggleValue)
            } else currentState
        }
    }

    fun updateDeleteDialogText(newText: String) {
        _mutableState.update { currentState ->
            if (currentState is ReviewUiState.Success) {
                currentState.copy(deleteHabitDialogText = newText)
            } else currentState
        }
    }

    fun currentYearMonthName(): String {
        val success = state.value as? ReviewUiState.Success ?: return ""
        val ym = success.currentYearMonth
        return "${ym.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${ym.year}"
    }


    @AssistedFactory
    interface Factory {
        fun create(habitId: Int): ReviewViewModel
    }
}
