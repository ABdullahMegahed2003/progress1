package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ExerciseCatalogItem(
    val id: String,
    val name: String,
    val nameArabic: String,
    val muscleGroup: String,
    val muscleArabic: String,
    val equipment: String,
    val equipmentArabic: String,
    val instructionsArabic: String = ""
)

@Entity(tableName = "plan_day_exercises")
data class PlanDayExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayOfWeek: Int, // 0 = السبت, 1 = الأحد, 2 = الإثنين, 3 = الثلاثاء, 4 = الأربعاء, 5 = الخميس, 6 = الجمعة
    val dayNameArabic: String,
    val splitId: String, // "ppl", "arnold", "upper_lower", "full_body"
    val exerciseId: String,
    val exerciseNameArabic: String,
    val muscleArabic: String,
    val targetSets: Int = 3,
    val targetReps: Int = 10,
    val orderIndex: Int = 0
)

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey
    val date: String, // "yyyy-MM-dd"
    val dayOfWeek: Int = 0,
    val status: String = "in_progress", // "completed", "in_progress", "rest", "skipped"
    val notes: String = "",
    val durationMinutes: Int = 0,
    val completedAt: Long = 0L
)

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "yyyy-MM-dd"
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Int,
    val weight: Float,
    val reps: Int,
    val isCompleted: Boolean = false
)
