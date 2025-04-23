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
        val fontUrl = "https://api.zhuti.intl.xiaomi.com/app/v9/uipages/font/$uuid"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(fontUrl).build()
        val response = okHttpClient.newCall(request).execute()
        val body = response.body?.string()
        //解析数据
        if (body!=null) {
            try {
                val jsonObject = JSONObject(body)
                val apiData = jsonObject.getJSONObject("apiData")
                val extraInfo = apiData.getJSONObject("extraInfo")
                val themeDetail = extraInfo.getJSONObject("themeDetail")

                //解析字体数据
                val downloadUrlRoot = themeDetail.getString("downloadUrlRoot")
                val downloadUrl1 = themeDetail.getString("downloadUrl")  //下载链接
                val downloadUrl = "$downloadUrlRoot/download/$downloadUrl1"
                val fileSize = themeDetail.getString("fileSize").toIntOrNull()  //字体大小

                Log.d("ThemeRepository", "parseFont: $downloadUrl")

                return@withContext Info.FontInfo(
                    fontUrl = downloadUrl,
                    fontSize = fileSize ?: 0
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return@withContext null
    }
}