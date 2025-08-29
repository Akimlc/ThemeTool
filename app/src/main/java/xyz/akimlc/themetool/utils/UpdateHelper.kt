package xyz.akimlc.themetool.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import xyz.akimlc.themetool.BuildConfig

object UpdateHelper {

    private fun checkUpdate(
        context: Context,
        respectIgnore: Boolean, // 是否遵循「忽略版本」设置
        onNewVersion: (version: String, changelog: String, downloadUrl: String) -> Unit,
        onLatest: (() -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://themetool.aixcert.top/api/update")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("UpdateCheck", "更新检查失败: ${response.code}")
                        return@use
                    }

                    val json = JSONObject(response.body.string())

                    val latestVersionName = json.optString("version_name")
                    val latestVersionCode = json.optInt("version_code")
                    val downloadUrl = json.optString("download_url")
                    val changelog = json.optString("changlog")

                    val currentVersionCode = BuildConfig.VERSION_CODE

                    // 是否被用户忽略
                    val ignored = PreferenceUtil.getBoolean("ignore_update_$latestVersionName")
                    val firstLaunch = PreferenceUtil.getBoolean("first_launch")

                    val hasUpdate = currentVersionCode < latestVersionCode
                    val shouldShow = hasUpdate && (!respectIgnore || (!ignored && firstLaunch))

                    withContext(Dispatchers.Main) {
                        if (shouldShow) {
                            Toast.makeText(
                                context,
                                "发现新版本 $latestVersionName！",
                                Toast.LENGTH_SHORT
                            ).show()
                            onNewVersion(latestVersionName, changelog, downloadUrl)
                        } else {
                            onLatest?.invoke()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun autoCheckUpdate(
        context: Context,
        onNewVersion: (version: String, changelog: String, downloadUrl: String) -> Unit
    ) {
        checkUpdate(context, respectIgnore = true, onNewVersion = onNewVersion)
    }

    /** 用户主动检查更新（无视忽略规则） */
    fun manualCheckUpdate(
        context: Context,
        onNewVersion: (version: String, changelog: String, downloadUrl: String) -> Unit,
        onLatest: () -> Unit
    ) {
        checkUpdate(
            context,
            respectIgnore = false,
            onNewVersion = onNewVersion,
            onLatest = onLatest
        )
    }

}