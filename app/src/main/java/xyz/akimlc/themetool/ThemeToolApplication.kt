package xyz.akimlc.themetool

import android.app.Application
import android.util.Log
import com.umeng.commonsdk.UMConfigure

class ThemeToolApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG){
            UMConfigure.setLogEnabled(true)
        }
        UMConfigure.preInit(this, "686a773c79267e0210a1d3db", "official") // 替换为你的 appKey 和渠道
        Log.d("ThemeTool", "Application 初始化成功！")

    }
}
