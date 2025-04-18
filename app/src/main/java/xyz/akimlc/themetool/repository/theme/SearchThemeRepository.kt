package xyz.akimlc.themetool.repository.theme

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.akimlc.themetool.data.model.Response
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel


object SearchThemeRepository {
    object HttpClient {
        val client = OkHttpClient()
    }

    suspend fun searchTheme(keywords: String): List<SearchThemeViewModel.ProductData> =
        withContext(Dispatchers.IO) {
            val url =
                "https://api.zhuti.xiaomi.com/app/v9/uipages/search/THEME/index?leftPrice=0&keywords=$keywords&miuiUIVersion=V150&region=CN"
            val request = Request.Builder().url(url).build()

            try {
                HttpClient.client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("请求失败: ${response.code}")
                    }

                    val jsonString = response.body?.string()
                        ?: throw IllegalArgumentException("Response Body is Null")

                    val result =
                        Json { ignoreUnknownKeys = true }
                            .decodeFromString<Response.BaseResponse<Response.DomesticThemeApiData>>(jsonString)

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
}