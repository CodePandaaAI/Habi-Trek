package com.liftley.habitrek.presentation.featureAddHabitScreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftley.habitrek.domain.model.Habit
import com.liftley.habitrek.domain.model.HabitColor
import com.liftley.habitrek.domain.usecase.CreateHabitUseCase
import com.liftley.habitrek.presentation.featureAddHabitScreen.model.AddHabitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddHabitViewModel @Inject constructor(private val createHabitUseCase: CreateHabitUseCase) :
    ViewModel() {

    companion object {
        val habitPalette: List<ULong> = listOf(
            0UL,
            0xFFE57373UL,
            0xFFF06292UL,
            0xFFBA68C8UL,
            0xFF9575CDUL,
            0xFF7986CBUL,
            0xFF64B5F6UL,
            0xFF4FC3F7UL,
            0xFF4DD0E1UL,
            0xFF4DB6ACUL,
            0xFF81C784UL,
            0xFFAED581UL,
            0xFFDCE775UL,
            0xFFFFD54FUL,
            0xFFFFB74DUL
        )
    }

    private val _mutableState = MutableStateFlow(AddHabitUiState())
    val state: StateFlow<AddHabitUiState> = _mutableState.asStateFlow()

    fun onHabitNameChange(name: String) {
        _mutableState.update { it.copy(habitUiModel = it.habitUiModel.copy(name = name)) }
    }

    fun onHabitDurationChange(newDurationValue: String) {
        val value = newDurationValue.filter { it.isDigit() }.toIntOrNull() ?: 0
        val durationMinutes = if (value > 1440) 0 else value
        _mutableState.update { it.copy(habitUiModel = it.habitUiModel.copy(durationMinutes = durationMinutes)) }
    }

    fun onHabitColorChange(index: Int) {
        _mutableState.update {
            it.copy(
                habitUiModel = it.habitUiModel.copy(
                    color = Color(
                        habitPalette[index]
                    )
                )
            )
        }
    }

    fun createHabit(onComplete: () -> Unit) {
        val habitName = state.value.habitUiModel.name
        val habitColor = state.value.habitUiModel.color.value
        val durationMinutes = state.value.habitUiModel.durationMinutes

        if (habitName.isEmpty() || durationMinutes <= 0) return

        val habit = Habit(
            id = 0,
            name = habitName,
            color = HabitColor(habitColor),
            durationMinutes = durationMinutes,
            isCompletedToday = false
        )

        viewModelScope.launch {
            createHabitUseCase(habit)
            onComplete()
        }
    }
}