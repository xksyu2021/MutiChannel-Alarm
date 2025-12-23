package com.example.mutichannel_alarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            "ACTION_ALARM_GET" -> {
                val alarmId = intent.getIntExtra("ALARM_ID", -1)
                val intentGet = Intent(context, AlarmGet::class.java).apply {
                    putExtra("ALARM_ID", alarmId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intentGet)
            }
            Intent.ACTION_BOOT_COMPLETED -> {

            }
            else -> 0
        }
    }
}

fun setAlarm(alarm: AlarmData,context: Context) {
    val time = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, alarm.timeHour)
        set(Calendar.MINUTE, alarm.timeMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DATE, 1)
        }
    }

    val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
        action = "ACTION_ALARM_GET"
        putExtra("ALARM_ID", alarm.id)
    }
    val alarmPendingIntent = PendingIntent.getBroadcast(
        context,
        alarm.id.toInt(),
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val showIntent = Intent(context, AlarmGet::class.java).apply {
        putExtra("ALARM_ID", alarm.id)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val showPendingIntent = PendingIntent.getActivity(
        context,
        alarm.id.toInt()+10000,
        showIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmClockInfo = AlarmClockInfo(time.timeInMillis,showPendingIntent)
    alarmManager.setAlarmClock(alarmClockInfo,alarmPendingIntent)
}