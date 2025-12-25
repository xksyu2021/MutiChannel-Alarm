package com.xksyu.mutichannel_alarm

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
fun onSave(temp :AlarmTemp, alarmViewModel: AlarmViewModel, context: Context,settingsManager: SettingsManager) : Boolean {
    var weekSelectTemp = 0b0
    when (temp.autoEnabled.value) {
        2 -> for (code in 0..1) {
            if(temp.autoDays[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
        1 -> for (code in 0..6) {
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

    setAlarm(db,context)
    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
    return true
}

@OptIn(ExperimentalMaterial3Api::class)
fun onSaveEdit(temp :AlarmTemp, alarmViewModel: AlarmViewModel, context: Context) : Boolean {
    var weekSelectTemp = 0b0
    when(temp.autoEnabled.value) {
        2 -> for (code in 0..1) {
            if(temp.autoDays[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
        1 -> for (code in 0..6) {
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
        setAlarm(alarm,context)
    }
    println("----------------SAVE----------------")
    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
    return true
}