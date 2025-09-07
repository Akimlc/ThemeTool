package xyz.akimlc.themetool.utils.font

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
import xyz.akimlc.themetool.utils.ImageUtils
import java.io.File
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class TTF2MTZ {
    companion object {
        private const val TAG = "TTF2MTZ"

        fun convert(
            context: Context,
            fontUri: Uri?,
            importFont: String,
            fontName: String,
            fontAuthor: String,
            onProgressUpdate: (Float) -> Unit,
            onFinish: () -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    onProgressUpdate(0f)

                    // 输出路径
                    val workDir = context.getExternalFilesDir("mtz")!!
                    val zipFile = File(workDir, "$fontName.mtz")
                    val fontsDir = File(workDir, "font/fonts").apply { mkdirs() }
                    val previewDir = File(workDir, "font/preview").apply { mkdirs() }
                    val configFile = File(workDir, "font/description.xml")

                    // 检查字体 Uri
                    requireNotNull(fontUri) { "字体 Uri 为空" }

                    // 复制字体文件
                    context.contentResolver.openInputStream(fontUri)?.use { input ->
                        FileOutputStream(File(fontsDir, importFont)).use { output ->
                            input.copyTo(output)
                        }
                    } ?: error("无法打开字体文件")
                    onProgressUpdate(0.2f)

                    // 生成字体预览图并移动
                    val previewFiles = ImageUtils().generateFontPreviewImages(
                        context,
                        File(fontsDir, importFont),
                        fontName
                    )
                    previewFiles.forEach { file ->
                        val dest = File(previewDir, file.name)
                        if (file.renameTo(dest)) {
                            Log.d(TAG, "图片移动成功: ${dest.absolutePath}")
                        } else {
                            Log.e(TAG, "图片移动失败: ${file.absolutePath}")
                        }
                    }

                    // 统一重命名字体
                    File(fontsDir, importFont).renameTo(File(fontsDir, "Roboto-Regular.ttf"))
                    onProgressUpdate(0.4f)

                    // 复制并修改 description.xml
                    context.assets.open("mtz/font/description.xml").use { input ->
                        FileOutputStream(configFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    val document =
                        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile)
                    document.documentElement.apply {
                        getElementsByTagName("title").item(0).textContent = fontName
                        getElementsByTagName("author").item(0).textContent = fontAuthor
                        getElementsByTagName("designer").item(0).textContent = fontAuthor
                    }
                    TransformerFactory.newInstance().newTransformer().apply {
                        setOutputProperty(OutputKeys.INDENT, "yes")
                        transform(DOMSource(document), StreamResult(configFile))
                    }
                    onProgressUpdate(0.6f)

                    // 压缩为 MTZ
                    FileUtils().zipDirectory(File(workDir, "font"), zipFile)
                    onProgressUpdate(0.8f)

                    // 移动到 Download/ThemeTool/MTZ
                    val downloadDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "ThemeTool/MTZ"
                    ).apply { mkdirs() }
                    val destinationFile = File(downloadDir, "$fontName.mtz")

                    val moved = zipFile.renameTo(destinationFile) || zipFile.copyTo(
                        destinationFile,
                        overwrite = true
                    ).exists()
                    if (moved) {
                        Log.d(TAG, "文件已保存到: ${destinationFile.absolutePath}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "字体文件已保存到 Download/ThemeTool/MTZ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        error("文件移动失败")
                    }

                    onProgressUpdate(1f)
                } catch (e: Exception) {
                    Log.e(TAG, "字体转换失败", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "字体转换失败: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                } finally {
                    onFinish()
                }
            }
        }
    }

}