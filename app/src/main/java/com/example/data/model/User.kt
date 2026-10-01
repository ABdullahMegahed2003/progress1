package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = "user_default",
    val name: String = "",
    val email: String = "",
    val birthDate: String = "", // e.g. "2000-01-01"
    val age: Int = 24,
    val weight: Float = 75f,
    val height: Float = 175f,
    val plan: String = "push_pull_legs", // push_pull_legs, arnold_split, upper_lower, full_body
    val profileImageUrl: String = "",
    val isLoggedIn: Boolean = false,
    val onboardingCompleted: Boolean = false
)
