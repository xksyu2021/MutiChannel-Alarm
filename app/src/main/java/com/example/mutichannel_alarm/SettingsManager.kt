package com.example.mutichannel_alarm

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(private val context: Context?) {
    companion object {
        private const val PREFS_NAME = "channel_settings"
        private const val CHAN_VIB = "vibrate"
        private const val CHAN_MODE = "mode"
    }
    private var previewMode: Int = 1
    private var previewVib: Boolean = false

    private val sharedPref: SharedPreferences? =
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    fun saveChanVib(able: Boolean) {
        if (context != null) {
            sharedPref?.edit()?.putBoolean(CHAN_VIB, able)?.apply()
        } else {
            previewVib = able
        }
    }
    fun saveChanMode(mode: Int) {
        if (context != null) {
            sharedPref?.edit()?.putInt(CHAN_MODE, mode)?.apply()
        } else {
            previewMode = mode
        }
    }
    fun getChanVib(): Boolean {
        return if (context != null) {
            sharedPref?.getBoolean(CHAN_VIB, false) ?: false
        } else {
            previewVib
        }
    }
    fun getChanMode(): Int {
        return if (context != null) {
            sharedPref?.getInt(CHAN_MODE, 1) ?: 1
        } else {
            previewMode
        }
    }
}