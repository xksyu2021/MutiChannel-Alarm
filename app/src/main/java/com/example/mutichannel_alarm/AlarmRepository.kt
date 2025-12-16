package com.example.mutichannel_alarm

class AlarmRepository(private val alarmDao: AlarmDataDao) {
    suspend fun insertAlarm(vararg data: AlarmData): Long {
        return alarmDao.insertAlarm(*data)
    }
    suspend fun insertAlarms(alarms: List<AlarmData>): Long {
        return alarmDao.insertAlarm(*alarms.toTypedArray())
    }
    suspend fun deleteAlarm(vararg data: AlarmData) {
        alarmDao.deleteAlarm(*data)
    }
    suspend fun deleteAlarms(alarms: List<AlarmData>) {
        alarmDao.deleteAlarm(*alarms.toTypedArray())
    }
    suspend fun updateAlarm(vararg data: AlarmData) {
        alarmDao.updateAlarm(*data)
    }
    suspend fun updateAlarms(alarms: List<AlarmData>) {
        alarmDao.updateAlarm(*alarms.toTypedArray())
    }
    suspend fun getUserById(id: Long): AlarmData? {
        return alarmDao.getUserById(id)
    }
    suspend fun getAllIds(): List<Long> {
        return alarmDao.getAllIds()
    }
}