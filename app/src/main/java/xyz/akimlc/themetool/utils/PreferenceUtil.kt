package xyz.akimlc.themetool.utils

import com.tencent.mmkv.MMKV

object PreferenceUtil {
    private val mmkv = MMKV.defaultMMKV()

    fun getInt(key: String, default: Int = 0): Int =
        mmkv.decodeInt(key, default)

    fun setInt(key: String, value: Int) =
        mmkv.encode(key, value)

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean =
        mmkv.decodeBool(key, defaultValue)

    fun setBoolean(key: String, value: Boolean) = mmkv.encode(key, value)

}