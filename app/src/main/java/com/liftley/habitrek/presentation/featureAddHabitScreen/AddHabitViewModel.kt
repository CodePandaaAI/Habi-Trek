package com.liftley.habitrek.presentation.featureAddHabitScreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _mutableState = MutableStateFlow(AddHabitUiState())
    val state: StateFlow<AddHabitUiState> = _mutableState.asStateFlow()

    fun onHabitNameChange(name: String) {
        _mutableState.update { it.copy(habitUiModel = it.habitUiModel.copy(name = name)) }
    }

    fun onDurationChange(newDurationValue: String) {
        val value = newDurationValue.filter { it.isDigit() }.toIntOrNull() ?: 0
        val durationMinutes = if (value > 1440) 0 else value
        _mutableState.update { it.copy(habitUiModel = it.habitUiModel.copy(durationMinutes = durationMinutes)) }
    }

    fun onColorChange(index: Int) {
        _mutableState.update { it.copy(habitUiModel = it.habitUiModel.copy(color = Color(state.value.habitPalette[index]))) }
    }

    fun createHabit(onComplete: () -> Unit) {
        val habitName = state.value.habitUiModel.name
        val habitColor = state.value.habitUiModel.color
        val durationMinutes = state.value.habitUiModel.durationMinutes

        viewModelScope.launch {
            createHabitUseCase(habitName, habitColor.value, durationMinutes)
            onComplete()
        }
    }
}