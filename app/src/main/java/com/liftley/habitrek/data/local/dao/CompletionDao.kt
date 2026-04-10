package com.liftley.habitrek.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.liftley.habitrek.data.local.entity.CompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletionDao {

    /*
    To Get list of all habits ID whose today's marked completed by finding which habits
    today millis is stored in completions table.
     */
    @Query("SELECT habitId FROM completions WHERE dateMillis = :dateMillis")
    fun getIdOfAllHabitsCompletedForDate(dateMillis: Long): Flow<List<Int>>

    /*
    Deleting Single Completion Representing single a day of a specific HabitEntity with ID and Date
     */
    @Query("DELETE FROM completions WHERE habitId = :habitId AND dateMillis = :dateMillis")
    suspend fun deleteCompletionWithIdAndDate(habitId: Int, dateMillis: Long)

    /*
    Checking if a Completion Entity Exists of specific habit with specific date millis
    "Is Usually used for Check Mark Button" for checking if completion exists for today then we should
    unmark/delete it or create new.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM completions WHERE habitId = :habitId AND dateMillis = :dateMillis)")
    suspend fun checkIfCompletionExistsWithHabitIdAndDate(habitId: Int, dateMillis: Long): Boolean

    /*
    Creating New CompletionEntity Thorough CheckMark Button or directly pressing dates in calendar
     */
    @Insert
    suspend fun addCompletion(completionEntity: CompletionEntity)

    /*
    Initializing ReviewScreen by fetching all Completion Entities of that Habit and marking
    calendar dates as completed or not
     */
    @Query("SELECT * FROM completions Where habitId = :habitId")
    fun getAllCompletionsForHabitWithId(habitId: Int): Flow<List<CompletionEntity>>
}