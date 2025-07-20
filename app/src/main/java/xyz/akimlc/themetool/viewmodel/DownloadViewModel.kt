package xyz.akimlc.themetool.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.akimlc.themetool.data.db.DownloadDao
import xyz.akimlc.themetool.data.db.DownloadEntity
import xyz.akimlc.themetool.data.model.DownloadStatus
import xyz.akimlc.themetool.utils.NetworkUtils.HttpClient
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.util.UUID

class DownloadViewModel(
    private val dao: DownloadDao
) : ViewModel() {
    private val TAG = "DownloadViewModel"

    val downloads = dao.getAllDownloads().stateIn(viewModelScope, SharingStarted.Lazily,emptyList())
    //存放下载列表
//    private val _downloads = MutableStateFlow<List<DownloadModel>>(emptyList())
//    val downloads: StateFlow<List<DownloadModel>> = _downloads

    var state by mutableStateOf(DownloadStatus.READY)

    fun fetchDownloadInfo(url: String, context: Context) {
        Log.d(TAG, "fetchDownloadInfo: 启动！！！！")
        viewModelScope.launch {
            val fileName = getFileNameFromUrl(url)
            val fileSize = getFileSizeFromUrl(url)

            val model = DownloadEntity(
                id = UUID.randomUUID().toString(),
                name = fileName,
                url = url,
                size = fileSize,
                progress = 0f,
                status = DownloadStatus.READY,
            )

           dao.insert(model)

            val outputDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "ThemeTool"
            )
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val outputFile = File(outputDir, fileName)

            startDownload(model, outputFile)
        }
    }

    //开始下载
    fun startDownload(model: DownloadEntity, outputFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = HttpClient.client
            val request = Request.Builder().url(model.url).build()
            dao.update(model.copy(status = DownloadStatus.DOWNLOADING))

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val body = response.body ?: throw IOException("Empty body")
                    val totalBytes = body.contentLength()
                    var downloadedBytes = 0L

                    val input = body.byteStream().buffered()
                    val output = FileOutputStream(outputFile)

                    val buffer = ByteArray(8192)
                    var read: Int

                    while (input.read(buffer).also { read = it }!=-1) {
                        output.write(buffer, 0, read)
                        downloadedBytes += read

                        val progress = downloadedBytes.toFloat() / totalBytes
                        dao.update(model.copy(progress = progress))
                    }

                    output.flush()
                    output.close()
                    input.close()

                    // 下载完成状态
                    dao.update(model.copy(progress = 1f, status = DownloadStatus.FINISHED))
                }
            } catch (e: Exception) {
                Log.e(TAG, "下载失败: ${e.message}")
                dao.update(model.copy(progress = 1f, status = DownloadStatus.FAILED))
            }
        }
    }

    fun clearDownloads() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearDownloads()
        }
    }

    private suspend fun getFileNameFromUrl(url: String): String {
        return withContext(Dispatchers.IO) {
            val fileName = url.substringAfterLast('/')
            try {
                URLDecoder.decode(fileName, "UTF-8")
            } catch (e: Exception) {
                fileName // decode 失败就返回原始名字
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private suspend fun getFileSizeFromUrl(url: String): Float {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).head().build()

            client.newCall(request).execute().use { response ->
                val contentLength = response.header("Content-Length")?.toLongOrNull() ?: -1L
                if (contentLength > 0) {
                    String.format("%.2f", contentLength / 1024f / 1024f).toFloat() // 转为 MB
                } else {
                    -1f
                }
            }
        }
    }


}