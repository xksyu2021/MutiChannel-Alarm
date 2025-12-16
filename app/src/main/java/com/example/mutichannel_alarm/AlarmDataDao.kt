package com.example.mutichannel_alarm
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDataDao {
    @Insert
    fun insertAlarm(vararg data: AlarmData) : Long
    @Delete
    fun deleteAlarm(vararg data: AlarmData)
    @Update
    suspend fun updateAlarm(vararg data: AlarmData)
    @Query("SELECT * FROM AlarmData WHERE id = :id")
    suspend fun getUserById(id: Long): AlarmData?
    @Query("SELECT id FROM AlarmData")
    suspend fun getAllIds(): List<Long>
}