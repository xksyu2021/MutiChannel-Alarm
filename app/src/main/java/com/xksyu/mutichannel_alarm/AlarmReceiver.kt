package com.xksyu.mutichannel_alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            "ACTION_ALARM_GET" -> {
                val alarmId = intent.getIntExtra("ALARM_ID", -1)
                println("DEBUG ALARM_ID in AlarmReceiver = $alarmId")
                val intentGet = Intent(context, AlarmGet::class.java).apply {
                    putExtra("ALARM_ID", alarmId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intentGet)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                CoroutineScope(Dispatchers.Main).launch{
                    reloadList(context)
                }
            }
        }
    }
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
    repository.alarms.collect {
        list -> list.forEach{
            alarm -> setAlarm(alarm,context)
        }
    }
}