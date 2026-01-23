package org.xksyu.mca.feature.basic

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.flow.first
import org.xksyu.mca.MCApplication
import org.xksyu.mca.data.base.AlarmData
import org.xksyu.mca.data.base.AlarmViewModel
import org.xksyu.mca.data.prefer.SettingsManager
import java.util.Calendar

fun setAlarm(alarm: AlarmData, context: Context,settingsManager: SettingsManager) {
    println("----------------setAlarm---------------")

    var dayOfWeek = 0b1 shl (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2)
    if (dayOfWeek == 0) dayOfWeek = 0b1 shl 6
    var time = when(alarm.autoWeek) {
        AlarmData.AUTO_EVERYDAY -> {
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

        AlarmData.AUTO_WEEK_OR_WEEKEND -> {
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

        AlarmData.AUTO_DIY -> {
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

                    alarm.timeMinute += alarm.remindMinute
                    if (alarm.timeMinute>=60){
                        alarm.timeMinute -= 60
                        alarm.timeHour += 1
                        if (alarm.timeHour>=24) alarm.timeHour-=24
                    }
                }else if (timeInMillis <= System.currentTimeMillis()){
                    add(Calendar.DATE,1)
                }
            }
        }
    }

    println("DEBUG AlarmData values:")
    println("  id: ${alarm.id}")
    println("  timeHour: ${alarm.timeHour}, timeMinute: ${alarm.timeMinute}")
    println("  name: ${alarm.name}")

    println(">>>> DEBUG MODE ** ${settingsManager.debugGet()} <<<<")

    if(settingsManager.debugGet() == SettingsManager.DEBUG_GRANT || settingsManager.debugGet() == SettingsManager.DEBUG_NOW ){
        time = Calendar.getInstance()
        time.add(Calendar.SECOND, 5)
    }

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

    println("DEBUG Set values:")
    println("  DayOfWeek: ${time.get(Calendar.DAY_OF_WEEK)}")
    println("  Hour: ${time.get(Calendar.HOUR_OF_DAY)}, Minute: ${time.get(Calendar.MINUTE)}, Second: ${time.get(Calendar.SECOND)}")

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time.timeInMillis,alarmPendingIntent)
}

fun cancelAlarm(alarm: AlarmData, context: Context){
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

@OptIn(ExperimentalMaterial3Api::class)
fun onSave(temp : AlarmTemp, alarmViewModel: AlarmViewModel, context: Context, settingsManager: SettingsManager) : Boolean {
    var weekSelectTemp = 0b0
    when (temp.autoEnabled.value) {
        AlarmTemp.AUTO_WEEK_OR_WEEKEND -> for (code in 0..1) {
            if(temp.autoDays[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
        AlarmTemp.AUTO_DIY -> for (code in 0..6) {
            if(temp.days[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
    }
    if(weekSelectTemp==0 && temp.autoEnabled.value in 1..2){
        Toast.makeText(context, "no day selected", Toast.LENGTH_LONG).show()
        return false
    }
    val db = AlarmData(
        id = settingsManager.updateId(),
        name = temp.text.value,
        timeHour = temp.hourGet.value,
        timeMinute = temp.minuteGet.value,
        autoWeek = temp.autoEnabled.value,
        remind = temp.remindEnabled.value,
        remindTime = temp.remindTimes.value,
        remindMinute = temp.remindMinutes.value,
        weekSelect = weekSelectTemp
    )
    alarmViewModel.insert(db)
    println("DEBUG AlarmData values:")
    println("  id: ${db.id}")
    println("  timeHour: ${db.timeHour}, timeMinute: ${db.timeMinute}")
    println("  name: ${db.name}")
    println("----------------SAVE----------------")

    setAlarm(db, context,settingsManager)
    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
    return true
}

@OptIn(ExperimentalMaterial3Api::class)
fun onSaveEdit(temp : AlarmTemp, alarmViewModel: AlarmViewModel, context: Context,settingsManager : SettingsManager) : Boolean {
    var weekSelectTemp = 0b0
    when(temp.autoEnabled.value) {
        AlarmTemp.AUTO_WEEK_OR_WEEKEND -> for (code in 0..1) {
            if(temp.autoDays[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
        AlarmTemp.AUTO_DIY -> for (code in 0..6) {
            if(temp.days[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
    }
    if(weekSelectTemp==0 && temp.autoEnabled.value in 1..2){
        Toast.makeText(context, "no day selected", Toast.LENGTH_LONG).show()
        return false
    }
    alarmViewModel.alarmById.value?.let { alarm ->
        with(alarm) {
            name = temp.text.value
            timeHour = temp.hourGet.value
            timeMinute = temp.minuteGet.value
            autoWeek = temp.autoEnabled.value
            remindTime = temp.remindTimes.value
            remindMinute = temp.remindMinutes.value
            remind = temp.remindEnabled.value
            weekSelect = weekSelectTemp
        }
        println("DEBUG AlarmData values:")
        println("  id: ${alarm.id}")
        println("  timeHour: ${alarm.timeHour}, timeMinute: ${alarm.timeMinute}")
        println("  name: ${alarm.name}")

        alarmViewModel.update(alarmViewModel.alarmById.value)
        setAlarm(alarm, context, settingsManager)
    }
    println("----------------SAVE----------------")
    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
    return true
}

suspend fun reloadList(context: Context,settingsManager: SettingsManager){
    val repository = (context.applicationContext as MCApplication).repository
    val list = repository.alarms.first()
    list.forEach { alarm ->
        setAlarm(alarm, context, settingsManager)
    }
}

fun repeatFun(repeatMode: Int,context: Context,alarmViewModel: AlarmViewModel,settingsManager : SettingsManager){
    val alarmRepeat = alarmViewModel.alarmById.value?.copy(
        id = settingsManager.updateId(),
        isRepeat = true, autoWeek = 0
    )
    alarmRepeat?.let {
        if (repeatMode == AlarmData.REPEAT_AUTO && it.remindTime > 0) {
            setAlarm(it, context,settingsManager)
            alarmViewModel.insert(it)
            it.remindTime -= 1
        }else if(repeatMode == AlarmData.REPEAT_MANUAL && it.remindTime > -1){
            setAlarm(it, context,settingsManager)
            alarmViewModel.insert(it)
        }
    }
    alarmViewModel.alarmById.value?.let {
        if(it.isRepeat) alarmViewModel.delete(it)
    }
}