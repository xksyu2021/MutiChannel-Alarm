package org.xksyu.mca.data.temp

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(private val context: Context?) {
    companion object {
        private const val PREFS_NAME = "channel_settings"
        private const val CHAN_VIB = "vibrate"
        private const val CHAN_MODE = "mode"
        private const val ID = "id"
        private const val IS_FIRST = "first_use"
        private const val LANG = "language" //auto=0 zh=1 en=2
        private const val WAY = "ways" //shizuku=1 def=2
        private const val DEBUG = "developer mode"
    }

    private val sharedPref: SharedPreferences? =
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    fun saveChanVib(able: Boolean) {
        sharedPref?.edit()?.putBoolean(CHAN_VIB, able)?.apply()
    }
    fun saveChanMode(mode: Int) {
        sharedPref?.edit()?.putInt(CHAN_MODE, mode)?.apply()
    }
    fun getChanVib(): Boolean {
        return sharedPref?.getBoolean(CHAN_VIB, false) ?: false
    }
    fun getChanMode(): Int {
        return sharedPref?.getInt(CHAN_MODE, 1) ?: 1
    }


    fun saveLang(lang: Int) {
        sharedPref?.edit()?.putInt(LANG, lang)?.apply()
    }
    fun getLang(): Int {
        return sharedPref?.getInt(LANG, 0) ?: 1
    }

    fun updateId() : Int{
        var idTemp = sharedPref?.getInt(ID, 0) ?: -2
        idTemp++
        if(idTemp!=-1)sharedPref?.edit()?.putInt(ID, idTemp)?.apply()
        return idTemp
    }
    fun isFirst() : Boolean{
        val isFirst = sharedPref?.getBoolean(IS_FIRST, true) ?: true
        return isFirst
    }

    fun notFirst(){
        sharedPref?.edit()?.putBoolean(IS_FIRST,  false)?.apply()
    }

    fun waySet(way: Int){
        sharedPref?.edit()?.putInt(WAY, way)?.apply()
    }
    fun wayGet(): Int{
        val way = sharedPref?.getInt(WAY, 2) ?: 2
        return way
    }

    fun debugSet(mode : Boolean){
        sharedPref?.edit()?.putBoolean(DEBUG, mode)?.apply()
    }
    fun debugGet(): Boolean{
        val way = sharedPref?.getBoolean(DEBUG, false) ?: false
        return way
    }
}