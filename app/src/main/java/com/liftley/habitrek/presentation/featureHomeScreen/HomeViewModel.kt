package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftley.habitrek.domain.repository.AiModelState
import com.liftley.habitrek.domain.usecase.DownloadAiModelUseCase
import com.liftley.habitrek.domain.usecase.GenerateAiSummaryUseCase
import com.liftley.habitrek.domain.usecase.GetCachedSummaryUseCase
import com.liftley.habitrek.domain.usecase.GetHabitsWithTodayStatusUseCase
import com.liftley.habitrek.domain.usecase.ObserveAiModelStateUseCase
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
    private val observeAiModelStateUseCase: ObserveAiModelStateUseCase,
    private val downloadAiModelUseCase: DownloadAiModelUseCase,
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
        observeAiModelState()
    }

    private fun observeHabits() {
        viewModelScope.launch {
            try {
                getHabitsWithTodayStatusUseCase().collect {
                    if (it.habits.isEmpty() && it.completedIdSet.isEmpty()) {
                        _mutableState.value = HomeUiState.Empty
                    } else {
                        val habits = it.toHomeUiModelList()
                        val isReady =
                            observeAiModelStateUseCase().value is AiModelState.Ready

                        // Preserve existing AI summary state if we already have it
                        val currentState = _mutableState.value
                        val existingSummary = (currentState as? HomeUiState.Success)?.aiSummary

                        _mutableState.value = HomeUiState.Success(
                            habits = habits,
                            isModelDownloaded = isReady,
                            aiSummary = existingSummary
                        )

                        // On first load, try to fetch today's cached summary from DB
                        if (existingSummary == null && isReady) {
                            loadCachedSummary()
                        }
                    }
                }
            } catch (e: Exception) {
                _mutableState.value = HomeUiState.Error(message = "Something Went Wrong")
            }
        }
    }

    private fun observeAiModelState() {
        viewModelScope.launch {
            observeAiModelStateUseCase().collect { modelState ->
                val currentState = _mutableState.value
                if (currentState is HomeUiState.Success) {
                    when (modelState) {
                        is AiModelState.NotDownloaded -> {
                            _mutableState.value = currentState.copy(
                                isModelDownloaded = false,
                                isDownloading = false
                            )
                        }

                        is AiModelState.Downloading -> {
                            _mutableState.value = currentState.copy(
                                isModelDownloaded = false,
                                isDownloading = true,
                                downloadProgress = modelState.progress,
                                downloadError = null
                            )
                        }

                        is AiModelState.Ready -> {
                            _mutableState.value = currentState.copy(
                                isModelDownloaded = true,
                                isDownloading = false,
                                downloadError = null
                            )
                            // When model becomes ready, try loading cached summary
                            if (currentState.aiSummary == null) {
                                loadCachedSummary()
                            }
                        }

                        is AiModelState.Error -> {
                            _mutableState.value = currentState.copy(
                                isModelDownloaded = false,
                                isDownloading = false,
                                downloadError = modelState.message
                            )
                        }
                    }
                }
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

    fun downloadAiModel() {
        val url =
            "https://github.com/CodePandaaAI/Habi-Trek/releases/download/v0.1.0-beta/gemma3-1b-it-int4.task"
        downloadAiModelUseCase(url)
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
                val freshState = _mutableState.value
                if (freshState is HomeUiState.Success) {
                    _mutableState.value = freshState.copy(
                        aiSummary = "AI Error: ${e.message}",
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