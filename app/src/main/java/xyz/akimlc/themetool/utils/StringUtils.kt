package xyz.akimlc.themetool.utils

import java.net.URLDecoder

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
     * 生成一个随机英文 + 数字的UA(别问，问就是标准的User-Agent访问不了)
     */
    fun generalRandomUA(length: Int = 12): String {
        val pool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { pool.random() }
            .joinToString("")
    }
}