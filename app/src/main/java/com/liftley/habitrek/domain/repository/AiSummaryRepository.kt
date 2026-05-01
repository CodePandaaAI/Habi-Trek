package com.liftley.habitrek.domain.repository

import kotlinx.coroutines.flow.StateFlow

/**
 * Domain-level contract for AI summary features.
 * The presentation layer uses this interface without knowing
 * about LiteRT LM, DownloadManager, or any SDK details.
 */
interface AiSummaryRepository {

    /** Observable download state for the UI to react to. */
    val downloadState: StateFlow<AiModelState>

    /** Whether the model file already exists on disk. */
    fun isModelReady(): Boolean

    /** Kick off the model download. */
    fun startModelDownload(url: String)

    /** Get today's cached summary from the database, or null if none exists. */
    suspend fun getCachedSummaryForToday(): String?

    /**
     * Generate a summary from current habit data, save it to the database,
     * and schedule resource cleanup after a delay.
     */
    suspend fun generateAndSaveSummary(): String

    /** Release native resources (engine memory). */
    fun releaseResources()
}

/** Domain-level representation of model download progress. */
sealed interface AiModelState {
    data object NotDownloaded : AiModelState
    data class Downloading(val progress: Int) : AiModelState
    data object Ready : AiModelState
    data class Error(val message: String) : AiModelState
}
