package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.repository.AiSummaryRepository
import com.liftley.habitrek.domain.repository.AiModelState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Observes the AI model's download/readiness state.
 */
class ObserveAiModelStateUseCase @Inject constructor(
    private val aiSummaryRepository: AiSummaryRepository
) {
    operator fun invoke(): StateFlow<AiModelState> {
        return aiSummaryRepository.downloadState
    }
}
