package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET profileImageUrl = :imageUrl WHERE id = :userId")
    suspend fun updateProfileImage(userId: String, imageUrl: String)

    @Query("UPDATE users SET plan = :plan WHERE id = :userId")
    suspend fun updatePlan(userId: String, plan: String)

    @Query("UPDATE users SET onboardingCompleted = :completed WHERE id = :userId")
    suspend fun updateOnboarding(userId: String, completed: Boolean)

    @Query("UPDATE users SET isLoggedIn = :loggedIn WHERE id = :userId")
    suspend fun updateLoginState(userId: String, loggedIn: Boolean)
}
