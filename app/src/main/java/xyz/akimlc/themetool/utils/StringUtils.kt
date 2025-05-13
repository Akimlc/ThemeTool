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
        // 随机 Android 版本
        val androidVersion = Random.nextInt(12, 16)

        // 随机设备型号
        val deviceModels = listOf(
            "MI 12", "MI 13", "MI 14", "MI 15",
            "Redmi K40", "Redmi K50", "Redmi K60", "Redmi K70", "Redmi K80"
        )
        val device = deviceModels.random()

        // 构建版本号生成
        val buildPrefixes = listOf("QKQ1", "RKQ1", "SKQ1", "TKQ1")
        val buildDate = "${Random.nextInt(190000, 200000)}"
        val buildSuffix = String.format("%03d", Random.nextInt(0, 999))
        val buildVersion = "${buildPrefixes.random()}.$buildDate.$buildSuffix"

        // 随机 Chrome 版本号
        val chromeMajor = Random.nextInt(100, 125)
        val chromeMinor = Random.nextInt(0, 1)
        val chromeBuild = Random.nextInt(4000, 7000)
        val chromePatch = Random.nextInt(50, 200)

        // 随机 Safari 版本
        val safariVersion = "537.${Random.nextInt(30, 60)}"

        return "Mozilla/5.0 (Linux; Android $androidVersion; $device Build/$buildVersion; wv) " +
                "AppleWebKit/$safariVersion (KHTML, like Gecko) Version/4.0 " +
                "Chrome/$chromeMajor.$chromeMinor.$chromeBuild.$chromePatch Mobile Safari/$safariVersion"
    }
}