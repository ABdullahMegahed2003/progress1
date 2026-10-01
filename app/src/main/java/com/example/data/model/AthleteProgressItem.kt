package com.example.data.model

data class AthleteProgressItem(
    val id: String,
    val name: String,
    val rankBadge: String, // "#1 🥇", "#2 🥈", "#3 🥉", "#4"
    val progressPercent: Float, // e.g. +34.5%
    val volumeKg: Float, // e.g. 54200 كجم
    val workoutsCompleted: Int,
    val streakDays: Int,
    val planName: String,
    val levelTitle: String, // "بطل ذهبي", "مستوى النخبة", "متقدم"
    val isCurrentUser: Boolean = false
)
