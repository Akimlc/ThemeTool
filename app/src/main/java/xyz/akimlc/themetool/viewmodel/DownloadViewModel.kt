package xyz.akimlc.themetool.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import xyz.akimlc.themetool.data.model.DownloadModel
import xyz.akimlc.themetool.data.model.DownloadStatus
import xyz.akimlc.themetool.data.model.Info
import java.util.UUID

class DownloadViewModel : ViewModel() {
    private val TAG = "DownloadViewModel"

    //存放下载列表
    private val _downloads = MutableStateFlow<List<DownloadModel>>(emptyList())
    val downloads: StateFlow<List<DownloadModel>> = _downloads

    //下载
    fun startDownload(themeInfo: Info.ThemeInfo) {
        val newDownload = DownloadModel(
            id = UUID.randomUUID().toString(),
            name = themeInfo.themeName,
            url = themeInfo.themeUrl,
            progress = 0f,
            size = themeInfo.themeSize / (1024 * 1024),
            status = DownloadStatus.NOT_STARTED
        )
        Log.d(TAG, "startDownload: 数据传递过来了:${newDownload.name},${newDownload.size},${newDownload.url}"
        )
        _downloads.value = _downloads.value.toMutableList().apply { add(newDownload) }
    }
}