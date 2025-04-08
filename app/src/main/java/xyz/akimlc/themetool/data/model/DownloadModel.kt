package xyz.akimlc.themetool.data.model

data class DownloadModel(
    val id: String,       // 下载ID
    val name: String,     // 下载名字
    val url: String,      // 下载链接
    val size: Int,   // 主题大小
    val progress: Float,  // 下载进度（0.0 - 100.0）
    val status: DownloadStatus // 下载状态
)

// 下载状态的枚举
enum class DownloadStatus {
    NOT_STARTED,  // 未开始
    DOWNLOADING,  // 下载中
    PAUSED,       // 已暂停
    COMPLETED,    // 下载完成
    FAILED        // 失败
}




