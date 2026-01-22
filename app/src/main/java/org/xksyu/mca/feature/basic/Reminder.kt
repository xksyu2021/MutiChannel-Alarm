package org.xksyu.mca.feature.basic

import android.content.Context
import android.content.Context.POWER_SERVICE
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import org.xksyu.mca.data.prefer.SettingsManager

class Reminder(private val context: Context, private val settingsManager: SettingsManager,private val id: Int) {
    private val vibrator =  context.getSystemService(Vibrator::class.java)
    private val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
    private val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MCA:WakeLockTag")
    private val handler = Handler(Looper.getMainLooper())
    private var idleRunnable: Runnable? = null

    private fun vibStart(){
        if (settingsManager.getChanVib() && settingsManager.debugGet() != SettingsManager.DEBUG_GRANT
        ) {
            vibrator?.vibrate(VibrationEffect.createOneShot(60 * 1000, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
    private fun wakelockStart(){
        wakeLock.setReferenceCounted(false)
        wakeLock.acquire(60 * 1000L)
    }


    fun idleAction(action: () -> Unit = {}) {
        idleRunnable =  Runnable{ action() }
    }
    fun start(){
        handler.postDelayed(idleRunnable!!, 60 * 1000L)
        vibStart()
        wakelockStart()
    }
    fun stop(){
        idleRunnable?.let { handler.removeCallbacks(it) }
        vibrator.cancel()
        AlarmForegroundService.stopNotification(context,id)
        AlarmForegroundService.stopService(context)
        wakeLock.release()
    }
}