package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    fun getLogForDateFlow(date: String): Flow<DailyLog?>

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getLogForDate(date: String): DailyLog?

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllLogsFlow(): Flow<List<DailyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DailyLog)

    @Update
    suspend fun update(log: DailyLog)

    @Query("DELETE FROM daily_logs WHERE date = :date")
    suspend fun deleteForDate(date: String)
}
