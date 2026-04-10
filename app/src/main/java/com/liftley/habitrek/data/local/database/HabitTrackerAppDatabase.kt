package com.liftley.habitrek.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.liftley.habitrek.data.local.dao.CompletionDao
import com.liftley.habitrek.data.local.dao.HabitDao
import com.liftley.habitrek.data.local.entity.CompletionEntity
import com.liftley.habitrek.data.local.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, CompletionEntity::class],
    exportSchema = false,
    version = 1
)
abstract class HabitTrackerAppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    abstract fun completionDao(): CompletionDao
}