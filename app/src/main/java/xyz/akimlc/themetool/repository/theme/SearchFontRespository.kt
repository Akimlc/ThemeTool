package xyz.akimlc.themetool.repository.theme

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Request
import xyz.akimlc.themetool.data.model.Response
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.viewmodel.FontSearchViewModel


class SearchFontRespository {

    suspend fun searchFont(keywords: String, viewModel: FontSearchViewModel,context: Context) {
        withContext(Dispatchers.IO) {
            val url =
                "https://thm.market.intl.xiaomi.com/thm/search/v2/npage?keywords=${keywords}&category=Font"
            val request = Request.Builder().url(url).build()

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
                val productDataList = result.apiData.cards.flatMap { card ->
                    (card.products?: emptyList()).map { product ->
                        FontSearchViewModel.ProductData(
                                name = product.name,
                                imageUrl = product.imageUrl,
                                uuid = product.uuid
                            )
                        }
                }
                withContext(Dispatchers.Main) {
                    if (productDataList.isEmpty()) {
                        Toast.makeText(context, "未找到相关字体", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.updateProductList(productDataList)
                }
            }
        }
    }
}