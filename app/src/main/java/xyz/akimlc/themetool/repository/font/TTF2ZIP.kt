package xyz.akimlc.themetool.repository.font

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.akimlc.themetool.utils.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

class TTF2ZIP {
    companion object {
        private const val TAG = "TTF2ZIP"

        fun convert(
            context: Context,
            fontUri: Uri?,
            importFont: String,
            fontName: String,
            onProgressUpdate: (Float) -> Unit,
            onFinish: () -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                onProgressUpdate(0f)
                // 创建临时目录结构
                val tempDir = File(context.getExternalFilesDir("zip"), "temp")
                val outputZipFile = File(context.getExternalFilesDir("zip"), "$fontName.zip")

                // 清理并创建目录
                if (tempDir.exists()) tempDir.deleteRecursively()
                tempDir.mkdirs()

                try {
                    if (fontUri==null) {
                        throw IllegalArgumentException("字体 Uri 为空")
                    }

                    // 获取模板ZIP文件
                    val templateZipFile = File(context.getExternalFilesDir("zip"), "template.zip")
                    
                    // 每次都删除旧的模板文件，强制从assets复制最新的
                    if (templateZipFile.exists()) {
                        templateZipFile.delete()
                    }
                    
                    // 从assets复制模板文件
                    val assetManager = context.assets
                    assetManager.open("template.zip").use { input ->
                        FileOutputStream(templateZipFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // 解压模板文件到临时目录
                    extractZip(templateZipFile, tempDir)
                    onProgressUpdate(0.3f)

                    // 获取导入的字体文件
                    val contentResolver = context.contentResolver
                    val inputStream = contentResolver.openInputStream(fontUri)
                        ?: throw Exception("无法打开字体文件")

                    // 创建system/fonts目录（如果不存在）
                    val fontDir = File(tempDir, "system/fonts")
                    if (!fontDir.exists()) fontDir.mkdirs()

                    // 复制字体文件到指定目录并重命名为fontchw4.ttf
                    val fontFile = File(fontDir, "fontchw4.ttf")
                    inputStream.use { input ->
                        FileOutputStream(fontFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    onProgressUpdate(0.6f)

                    // 压缩文件
                    FileUtils().zipDirectory(tempDir, outputZipFile)
                    onProgressUpdate(0.8f)

                    // 移动到下载目录
                    val downloadDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "ThemeTool"
                    )
                    if (!downloadDir.exists()) downloadDir.mkdirs()
                    val destinationFile = File(downloadDir, "$fontName.zip")
                    val moved = outputZipFile.renameTo(destinationFile)
                    onProgressUpdate(1f)

                    if (moved) {
                        Log.d(TAG, "文件已移动到: ${destinationFile.absolutePath}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "字体文件已保存到 Download/ThemeTool",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        throw Exception("文件移动失败")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "转换失败: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "字体转换失败: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                } finally {
                    // 清理临时文件
                    tempDir.deleteRecursively()
                    onFinish()
                }
            }
        }

        // 解压ZIP文件到指定目录
        private fun extractZip(zipFile: File, destDir: File) {
            ZipFile(zipFile).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val entryDestination = File(destDir, entry.name)

                    if (entry.isDirectory) {
                        entryDestination.mkdirs()
                    } else {
                        entryDestination.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            FileOutputStream(entryDestination).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        }
    }
}