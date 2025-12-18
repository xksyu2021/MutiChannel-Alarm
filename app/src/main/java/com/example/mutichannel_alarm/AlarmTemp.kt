package com.example.mutichannel_alarm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class AlarmTemp(
    var text: String = "default",
    var autoEnabled: Boolean = false,
    val autoDays: SnapshotStateList<Boolean> = mutableStateListOf(true, false),
    val days: SnapshotStateList<Boolean> = mutableStateListOf<Boolean>().apply { repeat(7) { add(false) } },
    var remindTimes: Int = 3,
    var remindMinutes: Int = 5,
    var remindEnabled: Boolean = true,
    var ringtone: String = "default",
    var hour: Int = 0,
    var minute: Int = 0
)