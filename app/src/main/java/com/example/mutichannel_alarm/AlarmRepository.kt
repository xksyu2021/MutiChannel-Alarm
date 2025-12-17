package com.example.mutichannel_alarm

class AlarmRepository(private val alarmDao: AlarmDataDao) {
    suspend fun insertAlarm(vararg data: AlarmData): Long {
        return alarmDao.insertAlarm(*data)
    }
    suspend fun deleteAlarm(vararg data: AlarmData) {
        alarmDao.deleteAlarm(*data)
    }
    suspend fun updateAlarm(vararg data: AlarmData) {
        alarmDao.updateAlarm(*data)
    }
    suspend fun getById(id: Long): AlarmData? {
        return alarmDao.getById(id)
    }
    suspend fun getAllIds(): List<Long> {
        return alarmDao.getAllIds()
    }
}