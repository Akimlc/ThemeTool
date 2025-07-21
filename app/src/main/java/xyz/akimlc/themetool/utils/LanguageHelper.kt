package xyz.akimlc.themetool.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

class LanguageHelper {

    companion object {
        fun getIndexLanguage(index: Int): Locale {
            return when (index) {
                0 -> Locale("zh", "CN") // 简体中文
                1 -> Locale("en")       // 英文
                2 -> Locale("Tr")   //土耳其
                else -> Locale("zh", "CN")
            }
        }

        fun wrap(context: Context): ContextWrapper {
            val resources = context.resources
            val config = Configuration(resources.configuration)
            val index = PreferenceUtil.getInt(context, "app_language")
            val locale = getIndexLanguage(index)

            config.setLocale(locale)
            Locale.setDefault(locale)

            val newContext = context.createConfigurationContext(config)
            return ContextWrapper(newContext)
        }

        fun Activity.setLocale(index: Int = PreferenceUtil.getInt(this, "app_language", 0)) {
            val res = this.resources
            val metrics = res.displayMetrics
            val configuration = res.configuration.apply {
                setLocale(LanguageHelper.getIndexLanguage(index))
            }
            res.updateConfiguration(configuration, metrics)
        }
    }

}