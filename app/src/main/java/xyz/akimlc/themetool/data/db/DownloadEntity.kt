package xyz.akimlc.themetool.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.akimlc.themetool.data.model.DownloadStatus

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val size: Float,
    val progress: Float = 0f,
    val status: DownloadStatus = DownloadStatus.READY,
    val timestamp: Long = System.currentTimeMillis()
)