package com.xksyu.mutichannel_alarm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class AlarmTemp(
    val text: MutableState<String> = mutableStateOf("default"),
    val autoEnabled: MutableState<Int> = mutableIntStateOf(0),
    val autoDays: SnapshotStateList<Boolean> = mutableStateListOf(true, false),
    val days: SnapshotStateList<Boolean> = mutableStateListOf<Boolean>().apply { repeat(7) { add(false) } },
    val remindTimes: MutableState<Int> = mutableIntStateOf(3),
    val remindMinutes: MutableState<Int> = mutableIntStateOf(5),
    val remindEnabled: MutableState<Boolean> = mutableStateOf(true),
    val ringtone: MutableState<String> = mutableStateOf("default"),
    val hour: MutableState<Int> = mutableIntStateOf(0),
    val minute: MutableState<Int> = mutableIntStateOf(0),
    val hourGet: MutableState<Int> = mutableIntStateOf(0),
    val minuteGet: MutableState<Int> = mutableIntStateOf(0)
)