package xyz.akimlc.themetool.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import xyz.akimlc.themetool.data.model.Info
import xyz.akimlc.themetool.utils.StringUtils

class ThemeRepository {
    suspend fun parseTheme(packId: String): Info.ThemeInfo? = withContext(Dispatchers.IO) {
        val themeInfoUrl =
            "https://thm.market.xiaomi.com/thm/download/v2/${packId}?miuiUIVersion=V150"
        val client = OkHttpClient()
        val request = Request.Builder().url(themeInfoUrl).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()

        if (body!=null) {
            try {
                val jsonObject = JSONObject(body)
                val apiData = jsonObject.getJSONObject("apiData")
                val downloadUrl = apiData.getString("downloadUrl")
                val fileSize = apiData.getString("fileSize").toIntOrNull()

                //根据下载链接获取主题名称
                val themeName = StringUtils().extractFileNameFromUrl(downloadUrl)
                if (fileSize!=null) {
                    return@withContext Info.ThemeInfo(
                        themeName = themeName,
                        themeUrl = downloadUrl,
                        themeSize = fileSize
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return@withContext null
    }

    suspend fun parseFont(uuid: String): Info.FontInfo? = withContext(Dispatchers.IO) {
        val fontUrl =
            "https://api.zhuti.intl.xiaomi.com/app/v9/uipages/font/$uuid?isGlobal=true&language=zh_CN&devicePixel=1080&device=apollo&region=MC"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(fontUrl)
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36"
            ).build()
        val response = okHttpClient.newCall(request).execute()
        val body = response.body?.string()
        //解析数据
        if (body!=null) {
            try {
                val jsonObject = JSONObject(body)
                val apiData = jsonObject.getJSONObject("apiData")
                val extraInfo = apiData.getJSONObject("extraInfo")
                val fontDetail = extraInfo.getJSONObject("themeDetail")
                //下载链接：fileServer + downloadUrl
                val fileServer = apiData.getString("fileServer")
                val downloadUrl = fontDetail.getString("downloadUrl")
                val fontName = fontDetail.getString("name")
                val downloadUrlRoot = "$fileServer$downloadUrl$fontName.mtz"
                val fileSize = fontDetail.getString("fileSize").toIntOrNull()  //字体大小
                return@withContext Info.FontInfo(
                    fontUrl = downloadUrlRoot,
                    fontSize = fileSize ?: 0
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return@withContext null
    }
}