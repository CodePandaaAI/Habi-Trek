package com.liftley.habitrek.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.liftley.habitrek.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    /*
    Creating a new Habit or Updating an existing one.
    - If id = 0 → inserts new Habit
    - If id exists → updates that Habit
     */
    @Upsert
    suspend fun upsertHabit(habitEntity: HabitEntity)

    /*
    Fetching all habits from database.
    - Used for Home Screen / List Screen
    - Returns Flow to automatically update UI when data changes
     */
    @Query("SELECT * FROM habit_table")
    fun getAllHabits(): Flow<List<HabitEntity>>

    /*
    Deleting a specific Habit using its unique id.
    - Usually triggered from delete button or swipe action
     */
    @Query("DELETE FROM habit_table WHERE id = :habitEntityId")
    suspend fun deleteHabit(habitEntityId: Int)

    /*
    Fetching a single Habit using its id.
    - Used when opening Review / Detail Screen of a Habit
     */
    @Query("SELECT * FROM habit_table WHERE id = :id")
    fun getHabitWithId(id: Int): Flow<HabitEntity>
}