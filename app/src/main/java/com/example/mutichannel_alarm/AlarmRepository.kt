package com.example.mutichannel_alarm

class AlarmRepository(private val alarmDao: AlarmDataDao) {
    suspend fun insertAlarm(data: AlarmData){
        alarmDao.insertAlarm(data)
    }
    suspend fun deleteAlarm(data: AlarmData) {
        alarmDao.deleteAlarm(data)
    }
    suspend fun updateAlarm(data: AlarmData) {
        alarmDao.updateAlarm(data)
    }
    suspend fun getById(id: Long): AlarmData? {
        return alarmDao.getById(id)
    }
    suspend fun getAllIds(): List<Long> {
        return alarmDao.getAllIds()
    }
}