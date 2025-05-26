package xyz.akimlc.themetool.repository.font

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Request
import xyz.akimlc.themetool.data.model.Response
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel

class SearchFontRepository {
    private val TAG = "SearchFontRepository"


    val json by lazy {
        Json { ignoreUnknownKeys = true }
    }

    suspend fun searchFont(keywords: String, page: Int): List<SearchFontViewModel.ProductData> =
        withContext(Dispatchers.IO) {
            val url =
                "https://thm.market.intl.xiaomi.com/thm/search/v2/npage?keywords=$keywords&category=Font&page=$page&language=zh_CN&devicePixel=1080&device=apollo&region=MC"
            val request = Request.Builder().url(url)
                .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36"
                )
                .build()

            NetworkUtils.HttpClient.client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Unexpected code $response")
                val responseBody = response.body?.string()
                    ?: throw IllegalArgumentException("Response Body is Null")


                //解析JSon
                val result =
                    json.decodeFromString<Response.BaseResponse<Response.GlobalFontApiData>>(
                        responseBody
                    )
                result.apiData.cards.flatMap { card ->
                    card.products?.map { product ->
                        SearchFontViewModel.ProductData(
                            name = product.name,
                            imageUrl = product.imageUrl,
                            uuid = product.uuid,
                        )
                    } ?: emptyList()
                }
            }
        }
}