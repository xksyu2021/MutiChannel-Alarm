package com.xksyu.mutichannel_alarm

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(private val context: Context?) {
    companion object {
        private const val PREFS_NAME = "channel_settings"
        private const val CHAN_VIB = "vibrate"
        private const val CHAN_MODE = "mode"
        private const val ID = "id"
        private const val IS_FIRST = "first_use"
        private const val LANG = "language"
    }
    private var previewMode: Int = 1
    private var previewVib: Boolean = false
    private var previewID : Int = -1
    private var previewFirst: Boolean = false
    private var previewLang : Int = 0

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


    fun saveLang(lang: Int) {
        if (context != null) {
            sharedPref?.edit()?.putInt(LANG, lang)?.apply()
        } else {
            previewMode = lang
        }
    }
    fun getLang(): Int {
        return if (context != null) {
            sharedPref?.getInt(LANG, 0) ?: 1
        } else {
            previewLang
        }
    }

    fun updateId() : Int{
        if (context != null) {
            var idTemp = sharedPref?.getInt(ID, 0) ?: -2
            idTemp++
            if(idTemp!=-1)sharedPref?.edit()?.putInt(ID, idTemp)?.apply()
            return idTemp
        } else {
            return previewID
        }
    }
    fun isFirst() : Boolean{
        if (context != null) {
            val isFirst = sharedPref?.getBoolean(IS_FIRST, true) ?: true
            if(isFirst) sharedPref?.edit()?.putBoolean(IS_FIRST,  false)?.apply()
            return isFirst
        } else {
            return previewFirst
        }
    }
}