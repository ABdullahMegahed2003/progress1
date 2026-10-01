package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class MealItem(
    val id: String = "",
    val title: String = "", // e.g. "وجبة 1", "وجبة بعد التمرين"
    val time: String = "", // e.g. "08:30 ص"
    val content: String = "", // e.g. "4 بيضات مسلوقة + شوفان مع موز وحليب"
    val calories: Int = 0
)

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey
    val date: String, // "yyyy-MM-dd"
    val sleepHours: Float = 7.5f,
    val sleepQuality: Int = 4, // 1 to 5
    val energyLevel: Int = 8, // 1 to 10
    val workHours: Float = 8.0f,
    val workExertion: String = "متوسط", // "خفيف", "متوسط", "شاق"
    val mealsJson: String = "[]", // Serialized List<MealItem>
    val workoutTime: String = "05:00 م",
    val notes: String = "",
    val bodyWeightKg: Float = 0f
)
