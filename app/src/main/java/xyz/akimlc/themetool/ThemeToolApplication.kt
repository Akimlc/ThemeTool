package xyz.akimlc.themetool

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.tencent.mmkv.MMKV
import com.umeng.commonsdk.UMConfigure
import xyz.akimlc.themetool.data.db.AppDatabase

class ThemeToolApplication : Application() {
    private val TAG = "ThemeToolApplication"
    companion object {
        lateinit var database: AppDatabase
    }
    override fun onCreate() {
        super.onCreate()

        //初始化MMKV
        MMKV.initialize(this)
        MMKV.defaultMMKV()

        //初始化Umeng
        if (BuildConfig.DEBUG){
            UMConfigure.setLogEnabled(true)
        }
        // TODO Key得硬编码保存
        UMConfigure.preInit(this, "686a773c79267e0210a1d3db", "official")
        Log.d("ThemeTool", "Application 初始化成功！")

        //初始化Room数据库
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "download.db"
        ).build()
        Log.d(TAG, "onCreate: download.db创建成功")
    }
}
