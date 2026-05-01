package com.liftley.habitrek.data.repository

import android.util.Log
import com.liftley.habitrek.data.ai.DownloadState
import com.liftley.habitrek.data.ai.GemmaSummarizer
import com.liftley.habitrek.data.ai.ModelDownloader
import com.liftley.habitrek.data.local.dao.AiSummaryDao
import com.liftley.habitrek.data.local.dao.CompletionDao
import com.liftley.habitrek.data.local.dao.HabitDao
import com.liftley.habitrek.data.local.entity.AiSummaryEntity
import com.liftley.habitrek.data.local.entity.HabitEntity
import com.liftley.habitrek.domain.repository.AiModelState
import com.liftley.habitrek.domain.repository.AiSummaryRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

private const val TAG = "AiSummaryRepo"
private const val RESOURCE_RELEASE_DELAY_MS = 2 * 60 * 1000L // 2 minutes
private const val MAX_HABITS_FOR_PROMPT = 6
private const val MAX_HABIT_NAME_LENGTH = 35

@Singleton
class AiSummaryRepositoryImpl @Inject constructor(
    private val modelDownloader: ModelDownloader,
    private val gemmaSummarizer: GemmaSummarizer,
    private val aiSummaryDao: AiSummaryDao,
    private val completionDao: CompletionDao,
    private val habitDao: HabitDao
) : AiSummaryRepository {

    // Structured scope with SupervisorJob — child failures don't cancel siblings
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var releaseJob: Job? = null

    private val _downloadState = MutableStateFlow(
        if (modelDownloader.isModelDownloaded()) AiModelState.Ready else AiModelState.NotDownloaded
    )
    override val downloadState: StateFlow<AiModelState> = _downloadState.asStateFlow()

    init {
        scope.launch {
            modelDownloader.downloadState.collect { dataState ->
                _downloadState.value = when (dataState) {
                    is DownloadState.NotDownloaded -> AiModelState.NotDownloaded
                    is DownloadState.Downloading -> AiModelState.Downloading(dataState.progress)
                    is DownloadState.Downloaded -> AiModelState.Ready
                    is DownloadState.Error -> AiModelState.Error(dataState.message)
                }
            }
        }
    }

    override fun isModelReady(): Boolean = modelDownloader.isModelDownloaded()

    override fun startModelDownload(url: String) {
        modelDownloader.startDownload(url)
    }

    override suspend fun getCachedSummaryForToday(): String? {
        val todayMillis = todayStartMillis()
        return aiSummaryDao.getSummaryForDate(todayMillis)?.summary
    }

    override suspend fun generateAndSaveSummary(): String {
        if (!modelDownloader.isModelDownloaded()) {
            throw IllegalStateException("Model not downloaded yet!")
        }

        // Cancel any pending resource release since we need the engine
        releaseJob?.cancel()

        // Build the smart compact prompt from DB data
        val prompt = buildSmartPrompt()
        Log.d(TAG, "Prompt: $prompt")

        // Initialize engine and generate
        gemmaSummarizer.initializeEngine(modelDownloader.modelFile.absolutePath)
        val result = gemmaSummarizer.generate(prompt)
        Log.d(TAG, "Generated summary: ${result.take(100)}...")

        // Save to Room
        val todayMillis = todayStartMillis()
        aiSummaryDao.upsertSummary(
            AiSummaryEntity(
                dateMillis = todayMillis,
                summary = result
            )
        )

        // Schedule resource release after 2 minutes
        scheduleResourceRelease()

        return result
    }

    override fun releaseResources() {
        Log.d(TAG, "Releasing engine resources")
        releaseJob?.cancel()
        gemmaSummarizer.close()
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

        // Build natural language data lines — easier for a 1B model to parse
        val dataLines = topHabits.mapIndexed { index, habit ->
            val name = habit.name.take(MAX_HABIT_NAME_LENGTH)
            val totalDays = completionCounts[habit.id] ?: 0
            val duration = habit.durationMinutes
            val doneToday = if (habit.id in todayCompletedIds) "done today" else "not done today"
            "${index + 1}. $name, completed $totalDays times total, $duration min per session, $doneToday"
        }.joinToString("\n")
        Log.d(TAG, "Data lines:\n$dataLines")

        // Clear separation: INSTRUCTIONS first, then DATA
        return """
Write a short casual summary about the user's habits in under 80 words. Rules:
- Start directly with the summary. No greetings. No "Here is" or "Okay" or any introduction.
- Write only one plain paragraph. No bullet points. No numbering. No bold. No italic. No asterisks. No special formatting.
- Use only simple everyday English words that everyone knows.
- Mention only the top 2 or 3 strongest habits by name.
- End with a short friendly reminder about one important habit that is not done today.
- Do not repeat or show the data below in your response.

User's habits:
$dataLines
        """.trimIndent()
    }

    // ── Helpers ───────────────────────────────────────────────

    private fun todayStartMillis(): Long {
        return LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    private fun scheduleResourceRelease() {
        releaseJob?.cancel()
        releaseJob = scope.launch {
            delay(RESOURCE_RELEASE_DELAY_MS)
            Log.d(TAG, "Timer expired — releasing engine resources")
            gemmaSummarizer.close()
        }
    }
}
