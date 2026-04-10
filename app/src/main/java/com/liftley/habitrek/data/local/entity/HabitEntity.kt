package com.liftley.habitrek.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_table")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Long,
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int
)