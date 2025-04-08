package xyz.akimlc.themetool.repository

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
        val themeInfoUrl = "https://thm.market.intl.xiaomi.com/thm/download/v2/${uuid}"
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
                if (fileSize!=null) {
                    return@withContext Info.FontInfo(fontUrl = downloadUrl, fontSize = fileSize)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return@withContext null
    }
}