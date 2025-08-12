package xyz.akimlc.themetool.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import xyz.akimlc.themetool.data.model.FontDetail
import xyz.akimlc.themetool.data.model.Info
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.utils.StringUtils

class Parse {
    private val TAG = "ThemeRepository"
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

    suspend fun parseFont(uuid: String): FontDetail? = withContext(Dispatchers.IO) {
        //val ua = StringUtils().generalRandomUA()
        val fontUrl =
            "https://api.zhuti.intl.xiaomi.com/app/v9/uipages/font/$uuid?isGlobal=true&language=zh_CN&devicePixel=1080&device=apollo&region=MC"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(fontUrl)
            .addHeader(
                "User-Agent",
                "okhttp/3.12.2"
            ).build()
        val response = okHttpClient.newCall(request).execute()
        val body = response.body?.string()
        //解析数据
        if (body!=null) {
            try {
                val jsonObject = JSONObject(body)
                val apiData = jsonObject.getJSONObject("apiData")
                val fileServer = apiData.getString("fileServer")
                val extraInfo = apiData.getJSONObject("extraInfo")
                val cards = apiData.getJSONArray("cards")

                val fontDetail = extraInfo.getJSONObject("themeDetail")
                //下载链接：fileServer + downloadUrl
                val downloadUrl = fontDetail.getString("downloadUrl")
                val fontName = fontDetail.getString("name")
                val downloadUrlRoot = "$fileServer$downloadUrl/$fontName.mtz"
                val fileSize = fontDetail.getString("fileSize").toIntOrNull()  //字体大小

                //获取作者名字和头像
                val designerCard = cards.getJSONObject(2)
                val authorName = designerCard.optString("designerName", " ")
                val authorIcon = designerCard.optString("designerIcon", " ")

                //获取预览图
                val overviewCard = cards.getJSONObject(0)
                val themeProductOverview = overviewCard.getJSONObject("themeProductOverview")
                val snapshotsArray = themeProductOverview.getJSONArray("snapshotsUrl")
                val previewUrl = List(snapshotsArray.length()) { i ->
                    snapshotsArray.getString(i)
                }
                Log.d(TAG, "parseFont: $fontName")
                return@withContext FontDetail(
                    fontName = fontName,
                    fontAuthor = authorName,
                    fontAuthorIcon = authorIcon,
                    fontDownloadUrl = downloadUrlRoot,
                    previewUrl = previewUrl
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return@withContext null
    }


    suspend fun parseGlobalTheme(uuid: String): Info.GlobalTheme? = withContext(Dispatchers.IO) {
        val url =
            "https://api.zhuti.intl.xiaomi.com/app/v9/uipages/theme/$uuid?devicePixel=1080&isGlobal=true&miuiUIVersion=V160"

        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).addHeader(
            "User-Agent",
            "Mozilla/5.0 (Linux; Android 12; M2007J3SC Build/SP1A.210812.016; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/86.0.4240.99 XWEB/4275 MMWEBSDK/20220204 Mobile Safari/537.36 MMWEBID/4275 MicroMessenger/8.0.20.2240(0x2800143C)WeChat/arm64 Weixin NetType/WIFI Language/zh"
        ).build()

        val response = okHttpClient.newCall(request).execute()
        val body = response.body?.string()

        if (body!=null) {
            try {
                val jsonObject = JSONObject(body)
                val apiData = jsonObject.getJSONObject("apiData")
                val extraInfo = apiData.getJSONObject("extraInfo")
                val themeDetail = extraInfo.getJSONObject("themeDetail")
                val fileServer = apiData.getString("fileServer")
                val downloadUrl = themeDetail.getString("downloadUrl")

                val name = themeDetail.getString("name")    //主题名字
                val fileSize = themeDetail.getString("fileSize")    //主题大小
                val themeDownloadUrl = "$fileServer$downloadUrl/$name.mtz" //下载链接
                return@withContext Info.GlobalTheme(
                    name = name,
                    fileSize = fileSize,
                    downloadUrl = themeDownloadUrl
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return@withContext null
    }


    suspend fun parseDomesticFont(uuid: String,name: String): Info.DomesticFontInfo? =
        withContext(Dispatchers.IO) {
            val url = "https://thm.market.xiaomi.com/thm/download/v2/$uuid"
            val request = Request.Builder().url(url).build()

            val response = NetworkUtils.HttpClient.client.newCall(request).execute()
            val body = response.body?.string()

            Log.d(TAG, "parseDomesticFont: $body")
            if (body!=null) {
                try {
                    val jsonObject = JSONObject(body)
                    val apiData = jsonObject.getJSONObject("apiData")
                    val downloadUrl = apiData.getString("downloadUrl")

                    return@withContext Info.DomesticFontInfo(
                        downloadUrl = downloadUrl,
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return@withContext null
        }


}