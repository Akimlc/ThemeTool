package xyz.akimlc.themetool.download

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.data.db.DownloadDao
import xyz.akimlc.themetool.data.model.DownloadStatus
import xyz.akimlc.themetool.data.model.DownloadTask
import java.io.FileOutputStream
import kotlin.coroutines.cancellation.CancellationException

class DownloadManager(
    private val context: Context,
    private val client: OkHttpClient,
    private val dao: DownloadDao,
    private val task: DownloadTask
) {
    private val TAG = "DownloadManager"
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    suspend fun execute() {
        var entity = task.model
        val file = task.outputFile
        try {
            val downloadedBytes = if (file.exists()) file.length() else 0L

            val request = Request.Builder().url(entity.url).apply {
                if (downloadedBytes > 0) {
                    header("Range", "bytes=$downloadedBytes-")
                }
            }.build()

            entity = entity.copy(status = DownloadStatus.DOWNLOADING)
            dao.update(entity)

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
                val body = response.body ?: throw Exception("Empty body")
                val totalBytes = body.contentLength() + downloadedBytes
                var downloaded = downloadedBytes

                val input = body.byteStream().buffered()
                val output = FileOutputStream(file, downloadedBytes > 0)

                val buffer = ByteArray(8192)
                var read: Int
                var lastUpdateTime = System.currentTimeMillis()
                var lastDownloaded = downloaded

                while (input.read(buffer).also { read = it }!=-1) {
                    output.write(buffer, 0, read)
                    downloaded += read

                    val now = System.currentTimeMillis()
                    if (downloaded - lastDownloaded > 100 * 1024 || now - lastUpdateTime > 1000) {
                        lastDownloaded = downloaded
                        lastUpdateTime = now

                        val progress = downloaded.toFloat() / totalBytes
                        entity = entity.copy(progress = progress)
                        dao.update(entity)

                        val notification = NotificationCompat.Builder(context, "download_channel")
                            .setSmallIcon(R.drawable.ic_download)
                            .setContentTitle("正在下载：${entity.name}")
                            .setProgress(100, (progress * 100).toInt(), false)
                            .setOngoing(true)
                            .build()
                        notificationManager.notify(entity.id.hashCode(), notification)
                    }
                }

                output.flush()
                output.close()
                input.close()

                entity = entity.copy(progress = 1f, status = DownloadStatus.FINISHED)
                dao.update(entity)
                notificationManager.cancel(entity.id.hashCode()) // 取消旧进度通知
                val done = NotificationCompat.Builder(context, "download_channel")
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentTitle("下载完成：${entity.name}")
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(entity.id.hashCode(), done) // 再发新通知
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                dao.update(entity.copy(status = DownloadStatus.STOP))
            } else {
                dao.update(entity.copy(status = DownloadStatus.FAILED))
            }
        }
    }
}