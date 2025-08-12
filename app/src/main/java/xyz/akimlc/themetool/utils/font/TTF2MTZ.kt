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
                onProgressUpdate(0f)
                val zipFile = File(context.getExternalFilesDir("mtz"), "$fontName.mtz")
                val fontsDir = File(context.getExternalFilesDir("mtz"), "font/fonts")
                val previewDir = File(context.getExternalFilesDir("mtz"), "font/preview")
                val configDir = File(context.getExternalFilesDir("mtz"), "font/description.xml")
                val configFile = File(context.getExternalFilesDir("mtz"), "font/description.xml")
                //生成文件夹
                if (!fontsDir.exists()) fontsDir.mkdirs()
                if (!previewDir.exists()) previewDir.mkdirs()

                try {
                    if (fontUri==null) {
                        throw IllegalArgumentException("字体 Uri 为空")
                    }
                    val contentResolver = context.contentResolver
                    val inputStream = contentResolver.openInputStream(fontUri)
                        ?: throw Exception("无法打开字体文件")
                    // 复制字体文件
                    val fontFile = File(fontsDir, importFont)
                    inputStream.use { input ->
                        FileOutputStream(fontFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    onProgressUpdate(0.2f)
                    //生成字体预览图片
                    val previewFiles =
                        ImageUtils().generateFontPreviewImages(context, fontFile, fontName)

                    //移动图片
                    for (file in previewFiles) {
                        val previewFileDest = File(previewDir, file.name)
                        if (file.renameTo(previewFileDest)) {
                            Log.d("SearchPage", "图片移动成功: ${previewFileDest.absolutePath}")
                        } else {
                            Log.e("SearchPage", "图片移动失败: ${file.absolutePath}")
                        }
                    }

                    //将字体文件重命名
                    fontFile.renameTo(File(fontsDir, "Roboto-Regular.ttf"))
                    onProgressUpdate(0.4f)

                    val assetManager = context.assets
                    assetManager.open("mtz/font/description.xml").use { inputStream ->
                        FileOutputStream(configDir).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    val document = docBuilder.parse(configFile)

                    //修改信息
                    val root = document.documentElement
                    val titleNameNode = root.getElementsByTagName("title").item(0)
                    val authorNode = root.getElementsByTagName("author").item(0)
                    val designerNode = root.getElementsByTagName("designer").item(0)
                    authorNode.textContent = fontAuthor
                    designerNode.textContent = fontAuthor
                    titleNameNode.textContent = fontName
                    onProgressUpdate(0.6f)
                    // 保存修改后的 description.xml
                    TransformerFactory.newInstance().newTransformer().apply {
                        setOutputProperty(OutputKeys.INDENT, "yes") // 美化输出
                        transform(DOMSource(document), StreamResult(configFile))
                    }
                    // 压缩文件
                    FileUtils().zipDirectory(
                        File(context.getExternalFilesDir("mtz"), "font"),
                        zipFile
                    )
                    onProgressUpdate(0.8f)

                    val downloadDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "ThemeTool/MTZ"
                    )
                    if (!downloadDir.exists()) downloadDir.mkdirs()
                    val destinationFile = File(downloadDir, "$fontName.mtz")
                    val moved = zipFile.renameTo(destinationFile)
                    onProgressUpdate(1f)
                    onFinish()
                    if (moved) {
                        Log.d(TAG, "文件已移动到: ${destinationFile.absolutePath}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "字体文件已保存到 Download/ThemeTool/MTZ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        throw Exception("文件移动失败")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "字体转换失败: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                } finally {
                    withContext(Dispatchers.IO) {
                        onFinish()

                    }
                }
            }
        }

    }
}