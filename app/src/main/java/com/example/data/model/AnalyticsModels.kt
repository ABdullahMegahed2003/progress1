package com.example.data.model

data class ProgressMetric(
    val totalVolumeKg: Float,
    val totalSets: Int,
    val totalReps: Int,
    val completedWorkoutsCount: Int
)

data class ComparisonResult(
    val currentPeriodVolume: Float,
    val previousPeriodVolume: Float,
    val percentageChange: Float, // e.g. +14.5%
    val currentSets: Int,
    val previousSets: Int
)

data class ExerciseHistoryPoint(
    val date: String,
    val maxWeightKg: Float,
    val estimatedOneRepMax: Float,
    val totalVolume: Float
)

data class LowestDayInsight(
    val date: String,
    val dayNameArabic: String,
    val volumeKg: Float,
    val reasonAnalysis: String
)

data class FactorCorrelation(
    val factorName: String,
    val impactScore: String, // e.g. "تأثير إيجابي قوي (+22% في الأوزان)"
    val description: String,
    val averageValue: String
)
