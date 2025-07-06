package xyz.akimlc.themetool

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import xyz.akimlc.themetool.ui.App
import xyz.akimlc.themetool.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
}