package xyz.akimlc.themetool.repository.font

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Request
import xyz.akimlc.themetool.data.model.GlobalFontResponse
import xyz.akimlc.themetool.data.model.Response
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel

class SearchFontRepository {
    suspend fun searchFont(keywords: String,page:Int): List<SearchFontViewModel.ProductData> =
        withContext(Dispatchers.IO) {
            val url =
                "https://thm.market.intl.xiaomi.com/thm/search/v2/npage?keywords=${keywords}&category=Font&page=$page"
            val request = Request.Builder().url(url).build()

            Log.d("SearchFontRepository", "searchFont: $url")
            NetworkUtils.HttpClient.client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Unexpected code $response")
                val responseBody = response.body?.string()
                    ?: throw IllegalArgumentException("Response Body is Null")

                //解析JSon
                val result = Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<Response.BaseResponse<Response.GlobalFontApiData>>(
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