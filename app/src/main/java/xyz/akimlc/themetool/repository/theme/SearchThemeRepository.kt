package xyz.akimlc.themetool.repository.theme

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Request
import xyz.akimlc.themetool.data.model.Response
import xyz.akimlc.themetool.data.network.GlobalThemeGlobal
import xyz.akimlc.themetool.utils.NetworkUtils.HttpClient
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel


object SearchThemeRepository {

    val json by lazy {
        Json { ignoreUnknownKeys = true }
    }

    suspend fun searchTheme(
        keywords: String,
        currentPage: Int
    ): List<SearchThemeViewModel.ProductData> =
        withContext(Dispatchers.IO) {
            val url =
                "https://api.zhuti.xiaomi.com/app/v9/uipages/search/THEME/index?leftPrice=0&keywords=$keywords&miuiUIVersion=V150&region=CN&cardStart=${currentPage}"
            val request = Request.Builder().url(url).build()

            try {
                HttpClient.client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("请求失败: ${response.code}")
                    }

                    val jsonString = response.body?.string()
                        ?: throw IllegalArgumentException("Response Body is Null")

                    val result =
                        json.decodeFromString<Response.BaseResponse<Response.DomesticThemeApiData>>(
                            jsonString
                        )

                    result.apiData.cards.flatMap { card ->
                        card.products?.map { product ->
                            SearchThemeViewModel.ProductData(
                                name = product.name,
                                imageUrl = product.imageUrl,
                                uuid = product.uuid,
                                author = product.author,
                                themeSize = product.fileSize
                            )
                        } ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                Log.e("Repository", "网络请求失败", e)
                throw e
            }
        }

    suspend fun searchGlobalTheme(
        keywords: String,
        page: Int
    ): List<SearchThemeViewModel.GlobalProductData> =
        withContext(Dispatchers.IO) {
            val url =
                "https://thm.market.intl.xiaomi.com/thm/search/v2/npage?category=Compound&keywords=$keywords&miuiUIVersion=V160&page=${page}&language=zh_CN&device=d&region=MC&isGlobal=true"
            val request = Request.Builder().url(url).addHeader(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 12; M2007J3SC Build/SP1A.210812.016; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/86.0.4240.99 XWEB/4275 MMWEBSDK/20220204 Mobile Safari/537.36 MMWEBID/4275 MicroMessenger/8.0.20.2240(0x2800143C)WeChat/arm64 Weixin NetType/WIFI Language/zh"
            ).build()

            try {
                HttpClient.client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("请求失败: ${response.code}")
                    }
                    val jsonString = response.body?.string()
                        ?: throw IllegalArgumentException("Response Body is Null")
                    val result = json.decodeFromString<GlobalThemeGlobal>(jsonString)
                    result.apiData.cards.flatMap { card ->
                        card.products.map { product ->
                            SearchThemeViewModel.GlobalProductData(
                                name = product.name,
                                imageUrl = product.imageUrl,
                                uuid = product.uuid
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Repository", "网络请求失败", e)
                throw e
            }

        }
}