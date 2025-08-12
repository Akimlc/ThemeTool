package xyz.akimlc.themetool

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.tencent.mmkv.MMKV
import com.umeng.commonsdk.UMConfigure
import xyz.akimlc.themetool.data.db.AppDatabase
class ThemeToolApplication : Application() {
    companion object {
        private lateinit var appContext: Context

        val database: AppDatabase by lazy {
            Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "download.db"
            ).build()
        }

        fun context(): Context = appContext
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        // 初始化 MMKV
        MMKV.initialize(this)
        MMKV.defaultMMKV()

        // 初始化友盟
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true)
        }
        UMConfigure.preInit(this, "686a773c79267e0210a1d3db", "official")

        Log.d("ThemeToolApplication", "Application 初始化完成")
    }
}
