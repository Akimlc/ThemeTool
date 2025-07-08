package xyz.akimlc.themetool.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

class LanguageHelper {

    companion object {
        private fun setLanguage(activity: Activity, language: String) {

            val context = activity.baseContext
            val resources = context.resources
            val configuration = resources.configuration
            val locale = Locale(language)
            val lastLocale = configuration.locales[0]
            Locale.setDefault(locale)
            configuration.setLocale(locale)
            activity.recreate()

        }

        private fun setLanguage(activity: Activity, language: String, country: String) {
            val context = activity.baseContext
            val resources = context.resources
            val configuration = resources.configuration
            val locale = Locale(language, country)
            val lastLocale = configuration.locales[0]

            Locale.setDefault(locale)
            configuration.setLocale(locale)
            //resources.updateConfiguration(configuration, resources.displayMetrics)
            activity.recreate()

        }

        private fun setLanguage(context: Context, locale: Locale?) {
            val resources = context.resources
            val configuration = resources.configuration
            if (locale!=null) {
                Locale.setDefault(locale)
            }
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        fun setIndexLanguage(activity: Activity, index: Int) {
            when (index) {
                0 -> setLanguage(activity, "zh", "CN")
                1 -> setLanguage(activity, "en")
            }
        }

        private fun getSystemLanguage(): Locale {
            val config = Resources.getSystem().configuration;
            return config.locales[0]
        }

        private fun getLanguage(language: String): Locale {
            return Locale(language)

        }

        private fun getLanguage(language: String?, country: String?): Locale {

            return Locale(language.toString(), country.toString())
        }

        fun getIndexLanguage(index: Int): Locale {
            return when (index) {
                0 -> Locale("zh", "CN") // 简体中文
                1 -> Locale("en")       // 英文
                else -> Locale("zh", "CN") // 默认强制中文
            }
        }

        fun wrap(context: Context): ContextWrapper {
            val resources = context.resources
            val config = Configuration(resources.configuration)
            val index = PreferenceUtil.getInt(context, "app_language", 0)
            val locale = getIndexLanguage(index)

            config.setLocale(locale)
            Locale.setDefault(locale)

            val newContext = context.createConfigurationContext(config)
            return ContextWrapper(newContext)
        }


    }

}