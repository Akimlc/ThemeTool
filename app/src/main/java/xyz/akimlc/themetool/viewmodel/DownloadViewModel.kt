package xyz.akimlc.themetool.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import xyz.akimlc.themetool.service.DownloadService
import java.net.URLDecoder
import java.util.UUID

class DownloadViewModel(
    private val dao: DownloadDao
) : ViewModel() {
    private val TAG = "DownloadViewModel"

    val downloads = dao.getAllDownloads().stateIn(viewModelScope, SharingStarted.Lazily,emptyList())

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
            DownloadService.startDownload(context,model)
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