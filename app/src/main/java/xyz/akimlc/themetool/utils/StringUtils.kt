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

}