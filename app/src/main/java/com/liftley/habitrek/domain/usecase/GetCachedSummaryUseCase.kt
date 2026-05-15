package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.repository.AiSummaryRepository
import javax.inject.Inject

/**
 * Retrieves today's cached AI summary from Room, if it exists.
 * Bypasses network generation to save latency.
 */
class GetCachedSummaryUseCase @Inject constructor(
    private val aiSummaryRepository: AiSummaryRepository
) {
    suspend operator fun invoke(): String? {
        return aiSummaryRepository.getCachedSummaryForToday()
    }
}
