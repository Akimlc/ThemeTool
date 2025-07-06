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

    // 设置用户是否同意隐私政策
    fun setUserAgreed(context: Context, agreed: Boolean) {
        getPreferences(context).edit { putBoolean(KEY_PRIVACY_AGREED, agreed) }
    }

    // 获取用户是否同意隐私政策
    fun isUserAgreed(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_PRIVACY_AGREED, false)
    }
}