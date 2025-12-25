package com.xksyu.mutichannel_alarm

import android.app.Application

class MCApplication : Application() {
    val repository: AlarmRepository by lazy {
        val dao = AppDatabase.getDatabase(this).AlarmDataDao()
        AlarmRepository(dao)
    }
}
