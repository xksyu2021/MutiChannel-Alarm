package org.xksyu.mca

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import org.xksyu.mca.data.base.AlarmRepository
import org.xksyu.mca.data.base.AppDatabase

class MCApplication : Application() {
    val repository: AlarmRepository by lazy {
        val dao = AppDatabase.getDatabase(this).AlarmDataDao()
        AlarmRepository(dao)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "alarm_channel_id",
            getString(R.string.notice),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "necessary"
            enableVibration(true)
            enableLights(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
