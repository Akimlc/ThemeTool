package xyz.akimlc.themetool.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class FileUtils {
    private val TAG = "FileUtils"
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }
    /**
     * 将目录压缩为 ZIP 文件
     *
     * @param sourceDir 源目录
     * @param zipFile   输出 ZIP 文件
     */
    fun zipDirectory(sourceDir: File, zipFile: File) {
        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            compressDirectoryToZipFile(sourceDir, sourceDir, zos)
        }
    }

    /**
     * 递归压缩目录中的文件
     *
     * @param rootDir 根目录，用于计算相对路径
     * @param sourceDir 当前文件或目录
     * @param zos ZIP 输出流
     */
    private fun compressDirectoryToZipFile(rootDir: File, sourceDir: File, zos: ZipOutputStream) {
        val files = sourceDir.listFiles() ?: return
        for (file in files) {
            val zipEntryName = file.relativeTo(rootDir).path
            if (file.isDirectory) {
                compressDirectoryToZipFile(rootDir, file, zos) // 递归处理子目录
            } else {
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry(zipEntryName)
                    zos.putNextEntry(zipEntry)
                    fis.copyTo(zos)
                    zos.closeEntry()
                }
            }
        }
    }
}


