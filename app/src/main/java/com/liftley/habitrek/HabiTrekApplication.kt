package com.liftley.habitrek

import android.app.Application
import com.liftley.habitrek.domain.repository.AiSummaryRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HabiTrekApplication : Application() {

    @Inject
    lateinit var aiSummaryRepository: AiSummaryRepository

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Level 10 = TRIM_MEMORY_RUNNING_LOW (deprecated constant)
        if (level >= 10) {
            aiSummaryRepository.releaseResources()
        }
    }
}