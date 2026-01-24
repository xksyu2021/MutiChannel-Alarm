package org.xksyu.mca.data.prefer

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsManager(context: Context?) {
    companion object {
        private const val PREFS_NAME = "channel_settings"
        private const val CHAN_VIB = "vibrate"
        private const val CHAN_MODE = "mode"
        private const val ID = "id"
        private const val IS_FIRST = "first_use"
        private const val LANG = "language" //auto=0 zh=1 en=2
        private const val WAY = "ways" //shizuku=1 def=2
        private const val DEBUG = "developer mode"

        const val LANG_AUTO = 0
        const val LANG_ZH = 1
        const val LANG_EN = 2
        const val WAY_SHIZUKU = 1
        const val WAY_DEFAULT = 2
        const val DEBUG_OFF = -1
        const val DEBUG_GRANT = -2
        const val DEBUG_NOW = 2
        const val DEBUG_VIEW = 3
        const val CHAN_PRIOR = 1
        const val CHAN_HP_ONLY = 2
        const val CHAN_SYSTEM = 3
        const val CHAN_SILENT = 4
    }

    private val sharedPref: SharedPreferences? =
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    fun saveChanVib(able: Boolean) {
        sharedPref?.edit {putBoolean(CHAN_VIB, able)}
    }
    fun saveChanMode(mode: Int) {
        sharedPref?.edit {putInt(CHAN_MODE, mode)}
    }
    fun getChanVib(): Boolean {
        return sharedPref?.getBoolean(CHAN_VIB, false) ?: false
    }
    fun getChanMode(): Int {
        return sharedPref?.getInt(CHAN_MODE, 1) ?: 1
    }


    fun saveLang(lang: Int) {
        sharedPref?.edit {putInt(LANG, lang)}
    }
    fun getLang(): Int {
        return sharedPref?.getInt(LANG, 0) ?: 1
    }

    fun updateId() : Int{
        var idTemp = sharedPref?.getInt(ID, 0) ?: -2
        idTemp++
        if(idTemp!=-1) sharedPref?.edit {putInt(ID, idTemp)}
        return idTemp
    }
    fun isFirst() : Boolean{
        val isFirst = sharedPref?.getBoolean(IS_FIRST, true) ?: true
        return isFirst
    }

    fun notFirst(){
        sharedPref?.edit {putBoolean(IS_FIRST,  false)}
    }

    fun waySet(way: Int){
        sharedPref?.edit { putInt(WAY, way) }
    }
    fun wayGet(): Int{
        val way = sharedPref?.getInt(WAY, 2) ?: 2
        return way
    }

    fun debugSet(mode : Int){
        sharedPref?.edit { putInt(DEBUG, mode) }
    }
    fun debugGet(): Int{
        val debug = sharedPref?.getInt(DEBUG, DEBUG_OFF) ?: DEBUG_OFF
        return debug
    }
    fun isDevDebug() : Boolean{
        return when (debugGet()) {
            DEBUG_OFF, DEBUG_GRANT -> false
            else -> true
        }
    }
}