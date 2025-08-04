package xyz.akimlc.themetool.service

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

class DownloadService : Service() {
    private val TAG = "DownloadService"
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dao = ThemeToolApplication.database.downloadDao()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url") ?: return START_NOT_STICKY
        val name = intent.getStringExtra("name") ?: return START_NOT_STICKY
        val id = intent.getStringExtra("id") ?: return START_NOT_STICKY

        // 输出目录
        val outputDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "ThemeTool"
        )
        if (!outputDir.exists()) outputDir.mkdirs()
        val outputFile = File(outputDir, name)

        scope.launch {
            val entity = dao.getById(id) ?: return@launch
            startDownload(DownloadTask(entity, outputFile))
        }

        return START_STICKY
    }

    private fun startDownload(task: DownloadTask) {
        scope.launch {
            try {
                var entity = task.model

                // 更新状态为 DOWNLOADING
                entity = entity.copy(status = DownloadStatus.DOWNLOADING)
                dao.update(entity)

                val request = Request.Builder().url(entity.url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw Exception("HTTP ${response.code}")

                    val body = response.body ?: throw Exception("Empty body")
                    val totalBytes = body.contentLength()
                    var downloaded = 0L

                    val input = body.byteStream().buffered()
                    val output = FileOutputStream(task.outputFile)
                    val buffer = ByteArray(8192)
                    var read: Int

                    while (input.read(buffer).also { read = it }!=-1) {
                        output.write(buffer, 0, read)
                        downloaded += read

                        // 计算进度 (0f~1f)
                        val progress = downloaded.toFloat() / totalBytes
                        entity =
                            entity.copy(progress = progress, status = DownloadStatus.DOWNLOADING)
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
                Log.e(TAG, "下载失败：${e.message}")
                // 失败状态
                val failed = task.model.copy(status = DownloadStatus.FAILED)
                dao.update(failed)
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        fun startDownload(context: android.content.Context, entity: DownloadEntity) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra("id", entity.id)
                putExtra("name", entity.name)
                putExtra("url", entity.url)
            }
            context.startService(intent)
        }
    }
}