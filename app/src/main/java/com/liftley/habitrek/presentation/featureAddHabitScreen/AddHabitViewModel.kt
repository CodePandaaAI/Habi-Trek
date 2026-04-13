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