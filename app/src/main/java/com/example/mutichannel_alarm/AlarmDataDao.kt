package com.example.mutichannel_alarm
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDataDao {
    @Insert
    suspend fun insertAlarm(vararg data: AlarmData)
    @Delete
    suspend fun deleteAlarm(vararg data: AlarmData?)
    @Update
    suspend fun updateAlarm(vararg data: AlarmData?)
    @Query("SELECT * FROM AlarmData WHERE id = :id")
    suspend fun getById(id: Int) : AlarmData?
    @Query("SELECT * FROM AlarmData")
    fun getAll(): Flow<List<AlarmData>>
}