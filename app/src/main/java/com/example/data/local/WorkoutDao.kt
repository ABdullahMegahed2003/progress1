package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PlanDayExercise
import com.example.data.model.WorkoutSessionEntity
import com.example.data.model.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Plan Exercises
    @Query("SELECT * FROM plan_day_exercises WHERE splitId = :splitId AND dayOfWeek = :dayOfWeek ORDER BY orderIndex ASC")
    fun getPlanExercisesForDay(splitId: String, dayOfWeek: Int): Flow<List<PlanDayExercise>>

    @Query("SELECT * FROM plan_day_exercises WHERE splitId = :splitId ORDER BY dayOfWeek ASC, orderIndex ASC")
    fun getAllPlanExercisesForSplit(splitId: String): Flow<List<PlanDayExercise>>

    @Query("SELECT COUNT(*) FROM plan_day_exercises WHERE splitId = :splitId")
    suspend fun getPlanExercisesCount(splitId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExercises(exercises: List<PlanDayExercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExercise(exercise: PlanDayExercise): Long

    @Query("DELETE FROM plan_day_exercises WHERE id = :id")
    suspend fun deletePlanExercise(id: Long)

    @Query("DELETE FROM plan_day_exercises WHERE splitId = :splitId")
    suspend fun clearSplit(splitId: String)

    // Sessions
    @Query("SELECT * FROM workout_sessions WHERE date = :date LIMIT 1")
    fun getSessionFlow(date: String): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE date = :date LIMIT 1")
    suspend fun getSession(date: String): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessionsFlow(): Flow<List<WorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)

    @Query("UPDATE workout_sessions SET status = :status WHERE date = :date")
    suspend fun updateSessionStatus(date: String, status: String)

    // Sets
    @Query("SELECT * FROM workout_sets WHERE date = :date ORDER BY id ASC")
    fun getSetsForDateFlow(date: String): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE isCompleted = 1")
    fun getAllCompletedSetsFlow(): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE exerciseName = :exerciseName AND isCompleted = 1 ORDER BY date ASC")
    fun getCompletedSetsForExerciseFlow(exerciseName: String): Flow<List<WorkoutSetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: WorkoutSetEntity): Long

    @Update
    suspend fun updateSet(set: WorkoutSetEntity)

    @Query("DELETE FROM workout_sets WHERE id = :id")
    suspend fun deleteSet(id: Long)

    @Query("DELETE FROM workout_sets WHERE date = :date AND exerciseName = :exerciseName")
    suspend fun deleteSetsForExercise(date: String, exerciseName: String)
}
