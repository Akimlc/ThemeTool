package xyz.akimlc.themetool

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.App
import xyz.akimlc.themetool.ui.compoent.UpdateDialog
import xyz.akimlc.themetool.ui.theme.AppTheme
import xyz.akimlc.themetool.utils.LanguageHelper
import xyz.akimlc.themetool.utils.PreferenceUtil

class MainActivity : ComponentActivity() {

    val showDialog = mutableStateOf(false)
    val latestVersionName = mutableStateOf("")
    val changelog = mutableStateOf("")
    val downloadUrl = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUpdate(
            context = this,
            onNewVersion = { version, log, url ->
                latestVersionName.value = version
                changelog.value = log
                downloadUrl.value = url
                showDialog.value = true
            }
        )
        AppSettingsState.language.intValue = PreferenceUtil.getInt("app_language", 0)
        AppSettingsState.colorMode.intValue = PreferenceUtil.getInt("color_mode", 0)
        setContent {
            AppTheme {
                App()
                UpdateDialog(
                    isShow = showDialog,
                    versionName = latestVersionName.value,
                    changelog = changelog.value,
                    downloadUrl = downloadUrl.value
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("UMengStats", "MainActivity onResume called - MobclickAgent.onResume")
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d("UMengStats", "MainActivity onPause called - MobclickAgent.onPause")
        MobclickAgent.onPause(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.wrap(newBase))
    }
}

private fun checkUpdate(
    context: Context,
    onNewVersion: (version: String, changelog: String, downloadUrl: String) -> Unit
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

                val body = response.body.string()
                val json = JSONObject(body)

                val latestVersionName = json.optString("version_name")
                val latestVersionCode = json.optInt("version_code")
                val downloadUrl = json.optString("download_url")
                val changelog = json.optString("changlog")
                val size = json.optLong("size")

                val currentVersionCode = BuildConfig.VERSION_CODE

                //判断是否勾选不再提示
                val ignored = PreferenceUtil.getBoolean("ignore_update_$latestVersionName")
                val firstLaunch = PreferenceUtil.getBoolean("first_launch")
                if (firstLaunch && !ignored && currentVersionCode < latestVersionCode) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "发现新版本 $latestVersionName！",
                            Toast.LENGTH_SHORT
                        ).show()
                        onNewVersion(latestVersionName, changelog, downloadUrl)
                    }
                } else {
                    Log.d("UpdateCheck", "用户选择忽略此版本，或已是最新版本")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
