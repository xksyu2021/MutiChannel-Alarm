package com.xksyu.mutichannel_alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
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
                    reloadList(context)
                }
            }
            "STOP_ALARM_ACTION" -> {
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
            "SNOOZE_ALARM_ACTION" -> {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val alarmId = intent.getIntExtra("ALARM_ID", -1)
                val settingsManager = SettingsManager(context)
                val repository = (context.applicationContext as MCApplication).repository
                CoroutineScope(Dispatchers.IO).launch {
                    val alarm = repository.getById(alarmId)
                    alarm?.let{
                        val repeatAlarm = it.copy(
                            id = settingsManager.updateId(),
                            isRepeat = true, autoWeek = 0
                        )
                        setAlarm(repeatAlarm,context)
                        repository.insertAlarm(repeatAlarm)
                        if (it.isRepeat){
                            repository.deleteAlarm(it)
                            println("NOTICE  AGAIN   id: ${it.id}")
                        }
                    }
                    val serviceIntent = Intent(context, AlarmForegroundService::class.java)
                    context.stopService(serviceIntent)
                    notificationManager.cancel(alarmId)
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
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(getString(R.string.notice_title))
            //.setContentText(repository.getById(alarmId)?.name ?: "null")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .setDeleteIntent(deletePendingIntent)
            .addAction(0, getString(R.string.notice_stop),stopPendingIntent)
            .addAction(0, getString(R.string.notice_later),snoozePendingIntent)
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

fun setAlarm(alarm: AlarmData,context: Context) {
    println("----------------setAlarm---------------")

    var dayOfWeek = 0b1 shl (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2)
    if (dayOfWeek == 0) dayOfWeek = 0b1 shl 6
    val time = when(alarm.autoWeek) {
        3 -> {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.timeHour)
                set(Calendar.MINUTE, alarm.timeMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()){
                    add(Calendar.DATE,1)
                }
            }
        }

        2 -> {
            println("  dayOfWeek: ${dayOfWeek.toString(2)}")
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.timeHour)
                set(Calendar.MINUTE, alarm.timeMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                val set: Int = when (alarm.weekSelect) {
                    0b1 -> 0b00_11111_00_11111
                    0b10 -> 0b11_00000_11_00000
                    else -> 0b11_11111_11_11111
                }
                println("  set: ${set.toString(2)}")
                for (count in 0..7) {
                    if (((dayOfWeek shl count) and set) != 0) {
                        println("  value: ${((dayOfWeek shl count) and set).toString(2)}, count: $count")
                        add(Calendar.DATE, count)
                        if (timeInMillis <= System.currentTimeMillis()) continue
                        break
                    }
                }
            }
        }

        1 -> {
            val setTemp = alarm.weekSelect
            val set = (setTemp shl 7) + alarm.weekSelect
            println("  dayOfWeek: ${dayOfWeek.toString(2)}, setTemp: ${setTemp.toString(2)}, set: ${set.toString(2)}")
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.timeHour)
                set(Calendar.MINUTE, alarm.timeMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                println("DEBUG week set")
                for (count in 0..7) {
                    if (((dayOfWeek shl count) and set) != 0) {
                        println("  value: ${((dayOfWeek shl count) and set).toString(2)}, count: $count")
                        add(Calendar.DATE, count)
                        if (timeInMillis <= System.currentTimeMillis()) continue
                        break
                    }
                }
            }
        }

        else -> {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.timeHour)
                set(Calendar.MINUTE, alarm.timeMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if(alarm.isRepeat){
                    add(Calendar.MINUTE,alarm.remindMinute)

                    //
                    //add(Calendar.SECOND,10)
                    //add(Calendar.MINUTE, - alarm.remindMinute)
                    //For debug

                    alarm.timeMinute += alarm.remindMinute
                    if (alarm.timeMinute>=60){
                        alarm.timeMinute -= 60
                        alarm.timeHour += 1
                        if (alarm.timeHour>=24) alarm.timeHour-=24
                    }
                }else if (timeInMillis <= System.currentTimeMillis()){
                    add(Calendar.DATE,7)
                }
            }
        }
    }

    println("DEBUG AlarmData values:")
    println("  id: ${alarm.id}")
    println("  timeHour: ${alarm.timeHour}, timeMinute: ${alarm.timeMinute}")
    println("  name: ${alarm.name}")
    println("DEBUG Set values:")
    println("  DayOfWeek: ${time.get(Calendar.DAY_OF_WEEK)}")
    println("  Hour: ${time.get(Calendar.HOUR_OF_DAY)}, Minute: ${time.get(Calendar.MINUTE)}")

    val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
        action = "ACTION_ALARM_GET"
        putExtra("ALARM_ID", alarm.id)
    }
    val alarmPendingIntent = PendingIntent.getBroadcast(
        context,
        alarm.id,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time.timeInMillis,alarmPendingIntent)
}

fun cancelAlarm(alarm: AlarmData,context: Context){
    println("====== Call cancelAlarm BEGIN ======")
    val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
        action = "ACTION_ALARM_GET"
        putExtra("ALARM_ID", alarm.id)
    }
    val alarmPendingIntent = PendingIntent.getBroadcast(
        context,
        alarm.id,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(alarmPendingIntent)
    alarmPendingIntent.cancel()
    println("====== Call cancelAlarm FINISHED ======")
}

suspend fun reloadList(context: Context){
    val repository = (context.applicationContext as MCApplication).repository
    val list = repository.alarms.first()
    list.forEach { alarm ->
        setAlarm(alarm, context)
    }
}