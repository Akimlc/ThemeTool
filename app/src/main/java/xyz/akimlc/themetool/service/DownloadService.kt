package xyz.akimlc.themetool.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.akimlc.themetool.ThemeToolApplication
import xyz.akimlc.themetool.data.db.DownloadEntity
import xyz.akimlc.themetool.data.model.DownloadStatus
import xyz.akimlc.themetool.data.model.DownloadTask
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.cancellation.CancellationException

class DownloadService : Service() {
    private val TAG = "DownloadService"
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dao = ThemeToolApplication.database.downloadDao()
    private val taskMap = mutableMapOf<String, Job>()
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action") ?: "start"
        val id = intent?.getStringExtra("id") ?: return START_NOT_STICKY

        if (action == "pause") {
            val job = taskMap[id]
            if (job != null && job.isActive) {
                job.cancel()
                scope.launch {
                    dao.getById(id)?.let {
                        dao.update(it.copy(status = DownloadStatus.STOP))
                    }
                }
            }
            return START_NOT_STICKY
        }

        val url = intent.getStringExtra("url") ?: return START_NOT_STICKY
        val name = intent.getStringExtra("name") ?: return START_NOT_STICKY

        val outputDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "ThemeTool"
        )
        if (!outputDir.exists()) outputDir.mkdirs()
        val outputFile = File(outputDir, name)

        val job = scope.launch {
            val entity = dao.getById(id) ?: return@launch
            startDownload(DownloadTask(entity, outputFile))
        }

        taskMap[id] = job
        return START_STICKY
    }

    private suspend fun startDownload(task: DownloadTask) {
        var entity = task.model
        val file = task.outputFile

        try {
            // 已下载字节数
            val downloadedBytes = if (file.exists()) file.length() else 0L

            // 构造 Range 请求
            val request = Request.Builder()
                .url(entity.url)
                .apply {
                    if (downloadedBytes > 0) {
                        header("Range", "bytes=$downloadedBytes-")
                    }
                }
                .build()

            // 更新状态为 DOWNLOADING
            entity = entity.copy(status = DownloadStatus.DOWNLOADING)
            dao.update(entity)

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
                val body = response.body ?: throw Exception("Empty body")

                val totalBytes = body.contentLength() + downloadedBytes
                var downloaded = downloadedBytes

                val input = body.byteStream().buffered()
                val output = FileOutputStream(file, /* append = */ downloadedBytes > 0)
                val buffer = ByteArray(8192)
                var read: Int

                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                    downloaded += read

                    val progress = downloaded.toFloat() / totalBytes
                    entity = entity.copy(progress = progress, status = DownloadStatus.DOWNLOADING)
                    dao.update(entity)
                }

                output.flush()
                output.close()
                input.close()

                // 下载完成
                entity = entity.copy(progress = 1f, status = DownloadStatus.FINISHED)
                dao.update(entity)
                Log.d(TAG, "下载完成：${entity.name}")
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                Log.w(TAG, "下载被暂停：${entity.name}")
                dao.update(entity.copy(status = DownloadStatus.STOP))
            } else {
                Log.e(TAG, "下载失败：${e.message}")
                dao.update(entity.copy(status = DownloadStatus.FAILED))
            }
        }
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