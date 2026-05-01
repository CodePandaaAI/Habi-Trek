package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.repository.AiSummaryRepository
import javax.inject.Inject

/**
 * Generates an AI summary from current habit data via the cloud SDK
 * and saves it to Room.
 */
class GenerateAiSummaryUseCase @Inject constructor(
    private val aiSummaryRepository: AiSummaryRepository
) {
    suspend operator fun invoke(): String {
        return aiSummaryRepository.generateAndSaveSummary()
    }
}
