package xyz.akimlc.themetool

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.umeng.analytics.MobclickAgent
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.App
import xyz.akimlc.themetool.ui.compoent.UpdateDialog
import xyz.akimlc.themetool.ui.theme.AppTheme
import xyz.akimlc.themetool.utils.LanguageHelper
import xyz.akimlc.themetool.utils.PreferenceUtil
import xyz.akimlc.themetool.utils.UpdateHelper

class MainActivity : ComponentActivity() {

    val showDialog = mutableStateOf(false)
    val latestVersionName = mutableStateOf("")
    val changelog = mutableStateOf("")
    val downloadUrl = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateHelper.autoCheckUpdate(this) { version, log, url ->
            showDialog.value = true
            latestVersionName.value = version
            changelog.value = log
            downloadUrl.value = url
        }
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
