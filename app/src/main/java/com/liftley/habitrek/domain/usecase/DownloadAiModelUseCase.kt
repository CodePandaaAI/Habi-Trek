package com.liftley.habitrek.domain.usecase

import com.liftley.habitrek.domain.repository.AiSummaryRepository
import javax.inject.Inject

/**
 * Triggers the AI model download.
 */
class DownloadAiModelUseCase @Inject constructor(
    private val aiSummaryRepository: AiSummaryRepository
) {
    operator fun invoke(url: String) {
        aiSummaryRepository.startModelDownload(url)
    }
}
