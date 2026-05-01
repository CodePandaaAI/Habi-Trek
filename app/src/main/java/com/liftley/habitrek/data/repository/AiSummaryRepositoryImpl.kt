package com.liftley.habitrek.data.repository

import android.util.Log
import com.liftley.habitrek.data.ai.GeminiSummarizer
import com.liftley.habitrek.data.local.dao.AiSummaryDao
import com.liftley.habitrek.data.local.dao.CompletionDao
import com.liftley.habitrek.data.local.dao.HabitDao
import com.liftley.habitrek.data.local.entity.AiSummaryEntity
import com.liftley.habitrek.data.local.entity.HabitEntity
import com.liftley.habitrek.domain.repository.AiSummaryRepository
import com.liftley.habitrek.presentation.util.toDurationString
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneOffset

private const val TAG = "AiSummaryRepo"
private const val MAX_HABITS_FOR_PROMPT = 6
private const val MAX_HABIT_NAME_LENGTH = 35

@Singleton
class AiSummaryRepositoryImpl @Inject constructor(
    private val geminiSummarizer: GeminiSummarizer,
    private val aiSummaryDao: AiSummaryDao,
    private val completionDao: CompletionDao,
    private val habitDao: HabitDao
) : AiSummaryRepository {

    override suspend fun getCachedSummaryForToday(): String? {
        val todayMillis = todayStartMillis()
        return aiSummaryDao.getSummaryForDate(todayMillis)?.summary
    }

    override suspend fun generateAndSaveSummary(): String {
        // Build the smart compact prompt from DB data
        val prompt = buildSmartPrompt()
        Log.d(TAG, "Prompt: $prompt")

        // Generate via cloud API
        val rawResult = geminiSummarizer.generate(prompt)
        Log.d(TAG, "Generated summary raw: ${rawResult.take(100)}...")

        // Clean up any formatting artifacts the model might have returned
        val cleanResult = cleanSummary(rawResult)
        
        // Save to Room
        val todayMillis = todayStartMillis()
        aiSummaryDao.upsertSummary(
            AiSummaryEntity(
                dateMillis = todayMillis,
                summary = cleanResult
            )
        )

        return cleanResult
    }

    // ── Post Processing ───────────────────────────────────────

    private fun cleanSummary(raw: String): String {
        return raw
            .lines()
            .filterNot { line ->
                // Drop lines that look like data format echoed back
                val lower = line.trimStart().lowercase()
                lower.startsWith("name of habit:") ||
                lower.startsWith("days completed:") ||
                lower.contains("target time everyday") ||
                lower.contains("user's habits") ||
                lower.startsWith("here is") ||
                lower.startsWith("okay")
            }
            .joinToString(" ")
            .replace(Regex("[*_#>]"), "")          // Strip markdown formatting
            .replace(Regex("^\\d+\\.\\s"), "")     // Strip leading "1. "
            .replace(Regex("- "), "")              // Strip bullet dashes
            .replace(Regex("\\s+"), " ")           // Collapse whitespace
            .trim()
            .take(400)                              // Hard cap length
    }

    // ── Smart Prompt Builder ──────────────────────────────────

    private suspend fun buildSmartPrompt(): String {
        val todayMillis = todayStartMillis()

        // Get all habits
        val allHabits: List<HabitEntity> = habitDao.getAllHabits().first()
        if (allHabits.isEmpty()) return "No habits tracked yet. Say: Start by adding your first habit!"

        // Get completion counts per habit
        val completionCounts = completionDao.getCompletionCountsPerHabit()
            .associate { it.habitId to it.totalCount }

        // Get today's completed habit IDs
        val todayCompletedIds = completionDao
            .getIdOfAllHabitsCompletedForDate(todayMillis)
            .first()
            .toSet()

        // Sort by total completions descending, take top 6
        val topHabits = allHabits
            .sortedByDescending { completionCounts[it.id] ?: 0 }
            .take(MAX_HABITS_FOR_PROMPT)

        // Build natural language data lines
        val dataLines = topHabits.mapIndexed { index, habit ->
            val name = habit.name.take(MAX_HABIT_NAME_LENGTH)
            val totalDays = completionCounts[habit.id] ?: 0
            val duration = habit.durationMinutes.toDurationString()
            val doneToday = habit.id in todayCompletedIds
            "${index + 1}. Name of habit: $name, Days completed: $totalDays, Target time everyday(Amount of time user wants to spend on this habit everyday): $duration, Is habit done today: $doneToday"
        }.joinToString("\n")
        Log.d(TAG, "Data lines:\n$dataLines")

        // Construct final prompt
        return """
Write a short casual overview summary about the user's habits in under 80 words. Rules to follow:

- Start directly with the summary. No greetings. No "Here is" or "Okay" or any introduction.
- Write only one plain paragraph. Please provide the response in plain text only, with no formatting or special characters.
- Use only simple everyday English words that everyone knows.
- ONLY say things the data supports. If there is little data, write a shorter summary.
- Mention little about only the top 3 or 4 strongest habits.
- End with a short friendly reminder about one important habit that is not done today.

User's habits full data to make summary from:
$dataLines
        """.trimIndent()
    }

    // ── Helpers ───────────────────────────────────────────────

    private fun todayStartMillis(): Long {
        return LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}
