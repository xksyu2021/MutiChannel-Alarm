package com.example.mutichannel_alarm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateOf

data class AlarmTemp(
    val text: MutableState<String> = mutableStateOf("default"),
    val autoEnabled: MutableState<Boolean> = mutableStateOf(false),
    val autoDays: SnapshotStateList<Boolean> = mutableStateListOf(true, false),
    val days: SnapshotStateList<Boolean> = mutableStateListOf<Boolean>().apply { repeat(7) { add(false) } },
    val remindTimes: MutableState<Int> = mutableStateOf(3),
    val remindMinutes: MutableState<Int> = mutableStateOf(5),
    val remindEnabled: MutableState<Boolean> = mutableStateOf(true),
    val ringtone: MutableState<String> = mutableStateOf("default"),
    val hour: MutableState<Int> = mutableStateOf(0),
    val minute: MutableState<Int> = mutableStateOf(0)
)