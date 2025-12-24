package com.example.mutichannel_alarm
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDataDao) {
    suspend fun insertAlarm(data: AlarmData){
        alarmDao.insertAlarm(data)
    }
    suspend fun deleteAlarm(data: AlarmData? = null) {
        alarmDao.deleteAlarm(data)
    }
    suspend fun updateAlarm(data: AlarmData?) {
        alarmDao.updateAlarm(data)
    }
    suspend fun getById(id: Int): AlarmData? {
        return alarmDao.getById(id)
    }
    val alarms: Flow<List<AlarmData>> = alarmDao.getAll()
}