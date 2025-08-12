package xyz.akimlc.themetool.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import xyz.akimlc.themetool.ThemeToolApplication
import xyz.akimlc.themetool.data.db.DownloadEntity
import xyz.akimlc.themetool.data.model.DownloadStatus
import xyz.akimlc.themetool.data.model.DownloadTask
import xyz.akimlc.themetool.download.DownloadManager
import java.io.File

class DownloadService : Service() {
    private val TAG = "DownloadService"
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dao = ThemeToolApplication.database.downloadDao()
    private val taskMap = mutableMapOf<String, Job>()
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "download_channel",
            "下载进度",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "用于展示下载进度"
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getStringExtra("id") ?: return START_NOT_STICKY
        val action = intent.getStringExtra("action") ?: "start"

        if (action=="pause") {
            taskMap[id]?.cancel()
            scope.launch {
                dao.getById(id)?.let {
                    dao.update(it.copy(status = DownloadStatus.STOP))
                }
            }
            return START_NOT_STICKY
        }
        val name = intent.getStringExtra("name") ?: return START_NOT_STICKY
        val outputDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "ThemeTool/Download"
        )
        if (!outputDir.exists()) outputDir.mkdirs()
        val outputFile = File(outputDir, name)
        val job = scope.launch {
            val entity = dao.getById(id) ?: return@launch
            val task = DownloadTask(entity, outputFile)
            val worker = DownloadManager(applicationContext, client, dao, task)
            worker.execute()
        }

        taskMap[id] = job
        return START_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }


    companion object {
        fun startDownload(context: Context, entity: DownloadEntity) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra("id", entity.id)
                putExtra("name", entity.name)
                putExtra("url", entity.url)
            }
            context.startService(intent)
        }

        fun pauseDownload(context: Context, entity: DownloadEntity) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra("id", entity.id)
                putExtra("action", "pause")
            }
            context.startService(intent)
        }
    }
}