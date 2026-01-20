package org.xksyu.mca.data.temp

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms

data class Permission(
    val exactAlarm: MutableState<Boolean> = mutableStateOf(false),
    val noticePermission: MutableState<Boolean> = mutableStateOf(false),
    val lockScreen: MutableState<Boolean> = mutableStateOf(false),
    val context: Context
){
    fun permissionCheck(){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        exactAlarm.value = canScheduleExactAlarms(alarmManager)

        val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        noticePermission.value = noticeManager.areNotificationsEnabled();
    }
}