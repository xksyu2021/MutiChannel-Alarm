package com.example.mutichannel_alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmData(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    var name :String = "alarm_$id",
    var timeHour :Int = 0,
    var timeMinute :Int = 0,

    //0=once 1=manual 2=auto
    var autoWeek : Int = 0,
    var weekSelect: Int = 0b00000_00,

    var remind : Boolean = true,
    var remindTime : Int = 3,
    var remindMinute : Int = 5,

    var isOpen : Boolean = true
)