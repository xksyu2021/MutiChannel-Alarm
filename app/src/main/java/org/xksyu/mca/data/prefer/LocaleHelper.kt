package org.xksyu.mca.data.prefer

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import java.util.Locale

object LocaleHelper {
    fun wrap(context: Context, language: Int): ContextWrapper {
        val langCode = when(language){
            SettingsManager.LANG_ZH -> "zh-CN"
            SettingsManager.LANG_EN -> "en"
            else -> ""
        }
        val locale = if (language == SettingsManager.LANG_AUTO) {
            Resources.getSystem().configuration.locales[0]
        } else {
            Locale.forLanguageTag(langCode)
        }
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration

        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val newContext = context.createConfigurationContext(configuration)
        return ContextWrapper(newContext)
    }
}