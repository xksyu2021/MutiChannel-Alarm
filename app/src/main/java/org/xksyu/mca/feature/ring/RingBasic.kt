package org.xksyu.mca.feature.ring

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import org.xksyu.mca.R
import org.xksyu.mca.data.prefer.SettingsManager


class RingBasic(private val context: Context,private val settingsManager: SettingsManager) {
    private val attributionContext: Context = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        context.createAttributionContext("alarm")
    } else { context }
    private var mediaPlayer :MediaPlayer? = null
    private var isReceiverRegistered = false
    private val noisyReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: android.content.Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                stopBasic()
            }
        }
    }

    private fun startBasic(){
        mediaPlayer?.apply{
            val defaultUri = attributionContext.resources.openRawResourceFd(R.raw.default_ring)
            setDataSource(defaultUri.fileDescriptor, defaultUri.startOffset, defaultUri.length)
            defaultUri.close()
            isLooping = true
            prepare()
            start()
        }
    }
    private fun stopBasic(){
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    private fun systemPlay(){
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        startBasic()
    }

    private fun findHeadphoneDevice(audioManager: AudioManager): AudioDeviceInfo? {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            val type = device.getType()
            if (type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                type == AudioDeviceInfo.TYPE_USB_HEADSET
            ) {
                return device
            }
        }
        return null
    }
    private fun headphonePlay() {
        val audioManager = context.getSystemService(AudioManager::class.java)
        val headphoneDevice = findHeadphoneDevice (audioManager)
        if (headphoneDevice != null) {
            if (!isReceiverRegistered) {
                val filter = android.content.IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                context.registerReceiver(noisyReceiver, filter)
                isReceiverRegistered = true
            }
            mediaPlayer?.setPreferredDevice(headphoneDevice)
            startBasic()
        }
    }

    fun play(){
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        when (settingsManager.getChanMode()) {
            SettingsManager.CHAN_SYSTEM -> { systemPlay() }
            SettingsManager.CHAN_HP_ONLY -> { headphonePlay() }
            SettingsManager.CHAN_SILENT -> { }
            else -> { startBasic() }
        }
    }
    fun stop(){
        if (isReceiverRegistered) {
            if (settingsManager.getChanMode() == SettingsManager.CHAN_HP_ONLY) {
                try {
                    context.unregisterReceiver(noisyReceiver)
                } catch (e: Exception) {
                }
            }
        }
        stopBasic()
    }
}