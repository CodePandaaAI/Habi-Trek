package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.repository.AiSummaryRepository
import javax.inject.Inject

/**
 * Checks Room for today's cached AI summary.
 * Returns it without touching the AI engine.
 */
class GetCachedSummaryUseCase @Inject constructor(
    private val aiSummaryRepository: AiSummaryRepository
) {
    suspend operator fun invoke(): String? {
        return aiSummaryRepository.getCachedSummaryForToday()
    }
}
