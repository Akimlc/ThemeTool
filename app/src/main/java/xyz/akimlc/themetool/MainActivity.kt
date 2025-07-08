package xyz.akimlc.themetool

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.umeng.analytics.MobclickAgent
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.App
import xyz.akimlc.themetool.ui.theme.AppTheme
import xyz.akimlc.themetool.utils.LanguageHelper
import xyz.akimlc.themetool.utils.PreferenceUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettingsState.language.intValue = PreferenceUtil.getInt(this, "app_language", 0)
        AppSettingsState.colorMode.intValue = PreferenceUtil.getInt(this, "color_mode", 0)
        setContent {
            AppTheme {
                App()
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