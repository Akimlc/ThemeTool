package xyz.akimlc.themetool

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.umeng.commonsdk.UMConfigure
import xyz.akimlc.themetool.data.db.AppDatabase

class ThemeToolApplication : Application() {
    private val TAG = "ThemeToolApplication"
    companion object {
        lateinit var database: AppDatabase
    }
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG){
            UMConfigure.setLogEnabled(true)
        }
        UMConfigure.preInit(this, "686a773c79267e0210a1d3db", "official")
        Log.d("ThemeTool", "Application 初始化成功！")

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "download.db"
        ).build()
        Log.d(TAG, "onCreate: download.db创建成功")
    }
}
