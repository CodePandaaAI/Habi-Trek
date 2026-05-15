package com.liftley.habitrek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_summaries")
data class AiSummaryEntity(
    @PrimaryKey val dateMillis: Long,
    val summary: String
)
