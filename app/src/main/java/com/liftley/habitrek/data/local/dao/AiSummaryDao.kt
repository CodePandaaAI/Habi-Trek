package com.liftley.habitrek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.liftley.habitrek.data.local.entity.AiSummaryEntity

@Dao
interface AiSummaryDao {

    @Query("SELECT * FROM ai_summaries WHERE dateMillis = :dateMillis LIMIT 1")
    suspend fun getSummaryForDate(dateMillis: Long): AiSummaryEntity?

    @Upsert
    suspend fun upsertSummary(entity: AiSummaryEntity)
}
