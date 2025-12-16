package com.example.mutichannel_alarm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmData(
    @PrimaryKey(autoGenerate = true)
    private val id :Int = 0,

    var name :String = "alarm_$id",
    var timeHour :Int = 0,
    var timeMinute :Int = 0,

    var autoWeek : Boolean = false,
    var weekSelect: MutableList<Boolean> = MutableList(7) { false },

    var remind : Boolean = true,
    var remindTime : Int = 3,
    var remindMinute : Int = 5
)