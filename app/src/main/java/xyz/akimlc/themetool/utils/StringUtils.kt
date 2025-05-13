package xyz.akimlc.themetool.utils

import android.annotation.SuppressLint
import java.net.URLDecoder
import kotlin.random.Random

class StringUtils {

    /**
     * 将URL编码转换成字符串
     * @param encoder URL字符编码
     * @return 解码后的字符串
     */
    fun decodeUrlString(encoder: String): String {
        return URLDecoder.decode(encoder, Charsets.UTF_8.name())
    }

    fun extractFileNameFromUrl(url: String): String {
        val lastSegment = url.substringAfterLast('/')
        val fileNameWithExtension = lastSegment.substringBeforeLast('.')
        val decodedName = URLDecoder.decode(fileNameWithExtension, "UTF-8")
        return decodedName
    }

    /**
     * 随机生成User-Agent
     */
    @SuppressLint("DefaultLocale")
    fun generalRandomUA(): String {
        val zeroCount = (5..30).random() // 随机 5 到 30 个 0
        val zeroPart = "0".repeat(zeroCount)
        return "okhttp/3.12.2$zeroPart"
    }
}