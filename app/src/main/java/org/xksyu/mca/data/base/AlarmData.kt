package org.xksyu.mca.data.base

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmData(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    var name :String = "alarm_$id",
    var timeHour :Int = 0,
    var timeMinute :Int = 0,

    var autoWeek : Int = 0,
    var weekSelect: Int = 0b00000_00,

    var remind : Boolean = true,
    var remindTime : Int = 3,
    var remindMinute : Int = 5,

    var isOpen : Boolean = true,
    var isRepeat : Boolean = false
) {
    companion object {
        const val AUTO_EVERYDAY = 3
        const val AUTO_WEEK_OR_WEEKEND = 2
        const val AUTO_DIY = 1
        const val AUTO_ONCE = 0

        const val REPEAT_AUTO = 0
        const val REPEAT_MANUAL = -1
    }
}