package xyz.akimlc.themetool.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferenceUtil {

    private const val PREF_NAME = "settings"
    private const val KEY_PRIVACY_AGREED = "privacy_agreed"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setUserAgreed(context: Context, agreed: Boolean) {
        getPreferences(context).edit { putBoolean(KEY_PRIVACY_AGREED, agreed) }
    }

    fun isUserAgreed(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_PRIVACY_AGREED, false)
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }


    fun putBoolean(context: Context, key: String, value: Boolean) {
        getPreferences(context).edit { putBoolean(key, value) }
    }

    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }

    fun putInt(context: Context, key: String, value: Int) {
        getPreferences(context).edit { putInt(key, value) }
    }
}