package xyz.akimlc.themetool.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

class ImageUtils {
    private val TAG = "ImageUtils"
    fun generateFontPreviewImages(
        context: Context, fontFile: File, previewText: String
    ): List<File> {

        val sizes = listOf(
            Pair(1080, 200) to "preview_fonts_small_0.jpg",
            Pair(1080, 1300) to "preview_fonts_0.jpg"
        )

        val typeface = Typeface.createFromFile(fontFile)
        val generatedFiles = mutableListOf<File>()

        for ((size, filename) in sizes) {
            val (width, height) = size

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)

            if (height > 300) {
                canvas.drawColor(Color.WHITE)
                val paint = TextPaint().apply {
                    this.typeface = typeface
                    this.textSize = 80f // 调整字体大小
                    this.color = Color.BLACK
                    this.isAntiAlias = true
                }
                val previewText = "由ThemeTool生成\n预览字体:\nHello, 123, Abc"
                val textWidth = width * 0.8f  // 文本宽度占比 80%，避免过宽

                val staticLayout = StaticLayout.Builder.obtain(
                    previewText,
                    0,
                    previewText.length,
                    paint,
                    textWidth.toInt()
                ).setAlignment(Layout.Alignment.ALIGN_NORMAL) // 左对齐
                    .setLineSpacing(10f, 1.2f) // 设置行距
                    .build()

                // 计算 Y 位置，顶部间距 50
                val x = (width - textWidth) / 2
                val y = 50f

                canvas.save()
                canvas.translate(x, y) // 移动画布到绘制起点
                staticLayout.draw(canvas)
                canvas.restore()

            } else {
                val paint = Paint().apply {
                    this.typeface = typeface
                    this.textSize = 120f
                    this.color = Color.BLACK
                    this.isAntiAlias = true
                }

                val textBounds = Rect()
                paint.getTextBounds(previewText, 0, previewText.length, textBounds)
                val textWidth = paint.measureText(previewText)
                val textHeight = textBounds.height()

                val x = (width - textWidth) / 2
                val y = (height + textHeight) / 2

                canvas.drawText(previewText, x, y.toFloat(), paint)
            }

            val previewFile = File(fontFile.parent, filename)
            FileOutputStream(previewFile).use { outputStream ->
                bitmap.compress(CompressFormat.PNG, 100, outputStream)
            }

            Log.d(TAG, "字体预览图片已生成: ${previewFile.absolutePath}")
            generatedFiles.add(previewFile)
        }

        return generatedFiles
    }
}