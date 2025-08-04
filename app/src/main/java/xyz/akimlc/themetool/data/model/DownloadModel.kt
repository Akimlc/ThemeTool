package xyz.akimlc.themetool.data.model

import xyz.akimlc.themetool.data.db.DownloadEntity
import java.io.File

data class DownloadModel(
    val id: String,       // 下载ID
    val name: String,     // 下载名字
    val url: String,      // 下载链接
    val size: Float,   // 主题大小
    val progress: Float,  // 下载进度（0.0 - 100.0）
    val status: DownloadStatus, // 下载状态
)

// 下载状态的枚举
enum class DownloadStatus {
    READY,  //准备
    FAILED, //失败
    DOWNLOADING, //下载中
    STOP, //暂停
    FINISHED    // 失败
}


data class DownloadTask(
    val model: DownloadEntity,
    val outputFile: File
)

