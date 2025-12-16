package com.example.mutichannel_alarm

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AlarmData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun AlarmDataDao(): AlarmDataDao
}