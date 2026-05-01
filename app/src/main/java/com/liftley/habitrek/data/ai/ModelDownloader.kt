package com.liftley.habitrek.data.ai

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import jakarta.inject.Inject
import jakarta.inject.Singleton

/** Raw download states internal to the data layer. */
sealed interface DownloadState {
    data object NotDownloaded : DownloadState
    data class Downloading(val progress: Int) : DownloadState
    data object Downloaded : DownloadState
    data class Error(val message: String) : DownloadState
}

/**
 * Data-source-level wrapper around Android's DownloadManager.
 * Same role as a Retrofit service — raw I/O access.
 */
@Singleton
class ModelDownloader @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    // Structured scope with SupervisorJob — cancellable and won't leak
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val modelFile = File(context.filesDir, "gemma3-1b-it-int4.task")

    private val _downloadState = MutableStateFlow(
        if (modelFile.exists()) DownloadState.Downloaded else DownloadState.NotDownloaded
    )
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    fun isModelDownloaded(): Boolean = modelFile.exists()

    fun startDownload(url: String) {
        if (isModelDownloaded()) {
            _downloadState.value = DownloadState.Downloaded
            return
        }

        val request = DownloadManager.Request(url.toUri())
            .setTitle("Downloading HabitAI Brain")
            .setDescription("Gemma 3 1B Model")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "gemma_temp.task"
            )
            .setAllowedOverMetered(false)
            .setAllowedOverRoaming(false)

        val downloadId = downloadManager.enqueue(request)
        _downloadState.value = DownloadState.Downloading(0)

        pollDownloadProgress(downloadId)
    }

    private fun pollDownloadProgress(downloadId: Long) {
        scope.launch {
            var downloading = true
            while (downloading) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)

                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val statusIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val downloadedIndex =
                            c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val totalIndex =
                            c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

                        if (statusIndex >= 0 && downloadedIndex >= 0 && totalIndex >= 0) {
                            val status = c.getInt(statusIndex)
                            val bytesDownloaded = c.getLong(downloadedIndex)
                            val bytesTotal = c.getLong(totalIndex)

                            when (status) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    downloading = false
                                    val uriIndex =
                                        c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                    if (uriIndex >= 0) {
                                        val uriStr = c.getString(uriIndex)
                                        if (uriStr != null) {
                                            finalizeDownload(uriStr.toUri())
                                            _downloadState.value = DownloadState.Downloaded
                                        } else {
                                            _downloadState.value =
                                                DownloadState.Error("Local URI is null")
                                        }
                                    }
                                }

                                DownloadManager.STATUS_FAILED -> {
                                    downloading = false
                                    _downloadState.value =
                                        DownloadState.Error("Download failed")
                                }

                                else -> {
                                    val progress =
                                        if (bytesTotal > 0) ((bytesDownloaded * 100L) / bytesTotal).toInt() else 0
                                    _downloadState.value = DownloadState.Downloading(progress)
                                }
                            }
                        }
                    }
                }
                delay(1000)
            }
        }
    }

    private fun finalizeDownload(tempUri: Uri) {
        try {
            context.contentResolver.openInputStream(tempUri)?.use { input ->
                modelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            _downloadState.value = DownloadState.Error("Failed to save model: ${e.message}")
        }
    }
}
