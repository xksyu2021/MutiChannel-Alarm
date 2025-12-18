package com.example.mutichannel_alarm
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDataDao {
    @Insert
    suspend fun insertAlarm(vararg data: AlarmData)
    @Delete
    suspend fun deleteAlarm(vararg data: AlarmData)
    @Update
    suspend fun updateAlarm(vararg data: AlarmData)
    @Query("SELECT * FROM AlarmData WHERE id = :id")
    suspend fun getById(id: Long): AlarmData?
    @Query("SELECT id FROM AlarmData")
    suspend fun getAllIds(): List<Long>
}