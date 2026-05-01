package com.liftley.habitrek.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.liftley.habitrek.BuildConfig
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data-source-level wrapper around the Gemini Cloud SDK.
 */
@Singleton
class GeminiSummarizer @Inject constructor() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    /**
     * Runs full inference via cloud API.
     */
    suspend fun generate(prompt: String): String = withContext(Dispatchers.IO) {
        val response = generativeModel.generateContent(prompt)
        response.text ?: throw IllegalStateException("Empty response from Gemini")
    }
}
