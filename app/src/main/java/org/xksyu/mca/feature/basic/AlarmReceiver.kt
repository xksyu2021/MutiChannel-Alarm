package org.xksyu.mca.feature.basic

import android.R
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.xksyu.mca.MCApplication
import org.xksyu.mca.data.prefer.SettingsManager
import org.xksyu.mca.page.AlarmGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        val settingsManager = SettingsManager(context)
        when(intent.action){
            "ACTION_ALARM_GET" -> {
                val alarmId = intent.getIntExtra("ALARM_ID", -1)
                println("DEBUG ALARM_ID in AlarmReceiver = $alarmId")

                val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
                    putExtra("ALARM_ID", alarmId)
                }
                ContextCompat.startForegroundService(context, serviceIntent)

            }
            Intent.ACTION_BOOT_COMPLETED -> {
                CoroutineScope(Dispatchers.Main).launch{
                    reloadList(context, settingsManager)
                }
            }
            "STOP_ALARM_ACTION" -> {
                if(!settingsManager.debugGet()) {
                    val alarmId = intent.getIntExtra("ALARM_ID", -1)
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val serviceIntent = Intent(context, AlarmForegroundService::class.java)
                    context.stopService(serviceIntent)
                    val repository = (context.applicationContext as MCApplication).repository
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarm = repository.getById(alarmId)
                        alarm?.let {
                            if (it.isRepeat) {
                                repository.deleteAlarm(it)
                            }
                            println("NOTICE  STOP   id: ${it.id}")
                        }
                        notificationManager.cancel(alarmId)
                    }
                }
            }
            "SNOOZE_ALARM_ACTION" -> {
                if(!settingsManager.debugGet()) {
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val alarmId = intent.getIntExtra("ALARM_ID", -1)
                    val settingsManager = SettingsManager(context)
                    val repository = (context.applicationContext as MCApplication).repository
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarm = repository.getById(alarmId)
                        alarm?.let {
                            val repeatAlarm = it.copy(
                                id = settingsManager.updateId(),
                                isRepeat = true, autoWeek = 0
                            )
                            setAlarm(repeatAlarm, context, settingsManager)
                            repository.insertAlarm(repeatAlarm)
                            if (it.isRepeat) {
                                repository.deleteAlarm(it)
                                println("NOTICE  AGAIN   id: ${it.id}")
                            }
                        }
                        val serviceIntent = Intent(context, AlarmForegroundService::class.java)
                        context.stopService(serviceIntent)
                        notificationManager.cancel(alarmId)
                    }
                }
            }
            "DELETE_ACTION" -> {
                val alarmId = intent.getIntExtra("ALARM_ID", -1)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val serviceIntent = Intent(context, AlarmForegroundService::class.java)
                context.stopService(serviceIntent)
                val repository = (context.applicationContext as MCApplication).repository
                CoroutineScope(Dispatchers.IO).launch {
                    val alarm = repository.getById(alarmId)
                    alarm?.let {
                        if (it.isRepeat) {
                            repository.deleteAlarm(it)
                        }
                        println("NOTICE  STOP   id: ${it.id}")
                    }
                    notificationManager.cancel(alarmId)
                }
            }
        }
    }
}

class AlarmForegroundService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)

        val fullScreenIntent = Intent(this, AlarmGet::class.java).apply {
            putExtra("ALARM_ID", alarmId)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
        }
        val pendingIntent = PendingIntent.getActivity(
            this, alarmId, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = "STOP_ALARM_ACTION"
            putExtra("ALARM_ID", alarmId)
        }
        val snoozeIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = "SNOOZE_ALARM_ACTION"
            putExtra("ALARM_ID", alarmId)
        }
        val deleteIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = "DELETE_ACTION"
            putExtra("ALARM_ID", alarmId)
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val snoozePendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId+ 1000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val deletePendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId +1001,
            deleteIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val repository = (application as MCApplication).repository
        val notificationBuilder = NotificationCompat.Builder(this, "alarm_channel_id")
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setContentTitle(getString(org.xksyu.mca.R.string.notice_title))
            //.setContentText(repository.getById(alarmId)?.name ?: "null")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .setDeleteIntent(deletePendingIntent)
            .addAction(0, getString(org.xksyu.mca.R.string.notice_stop),stopPendingIntent)
            .addAction(0, getString(org.xksyu.mca.R.string.notice_later),snoozePendingIntent)
            .setOngoing(true)

        CoroutineScope(Dispatchers.IO).launch {
            println("----------------NOTICE---------------")
            repository.getById(alarmId)?.let{
                val name = it.name
                println("NOTICE DEBUG AlarmData values:")
                println("NOTICE  id: ${it.id}")
                println("NOTICE  timeHour: ${it.timeHour}, timeMinute: ${it.timeMinute}")
                println("NOTICE  name: ${it.name}")
                notificationBuilder.setContentText(name)
            }
            startForeground(alarmId, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            println("----------------NOTICE END---------------")
        }
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
}