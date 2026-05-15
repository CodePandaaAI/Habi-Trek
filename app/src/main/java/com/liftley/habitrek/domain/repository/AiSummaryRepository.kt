package com.liftley.habitrek.domain.repository

/**
 * Domain-level contract for AI summary features.
 * The presentation layer uses this interface without knowing
 * about the Gemini Cloud SDK or any networking details.
 */
interface AiSummaryRepository {

    /** Get today's cached summary from the database, or null if none exists. */
    suspend fun getCachedSummaryForToday(): String?

    /**
     * Generate a summary from current habit data via cloud API
     * and save it to the database.
     */
    suspend fun generateAndSaveSummary(): String
}
