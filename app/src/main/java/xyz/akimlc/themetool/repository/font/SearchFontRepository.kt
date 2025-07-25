package xyz.akimlc.themetool.repository.font

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Request
import xyz.akimlc.themetool.data.model.response.font.DomesticFontModel
import xyz.akimlc.themetool.data.model.response.font.InternationFontModel
import xyz.akimlc.themetool.ui.page.font.Region
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel

class SearchFontRepository {
    private val TAG = "SearchFontRepository"
    suspend fun searchFont(
        region: Region,
        keyword: String,
        version: String,
        page: Int
    ): List<SearchFontViewModel.ProductData> {
        val json by lazy {
            Json { ignoreUnknownKeys = true }
        }
        return withContext(Dispatchers.IO) {
            val baseUrl = when (region) {
                Region.DOMESTIC ->  //国内
                    "https://api.zhuti.xiaomi.com/app/v9/uipages/search/FONT/index?keywords=${keyword}&miuiUIVersion=$version&cardStart=$page"

                Region.INTERNATIONAL -> //  国际
                    "https://thm.market.intl.xiaomi.com/thm/search/v2/npage?keywords=$keyword&category=Font&page=$page&language=zh_CN&device=fuxi&region=MC&isGlobal=true"
            }


            Log.d(
                TAG,
                "searchFont: 传入的参数:Region = $region,keyword = $keyword,page = $page,$version"
            )

            //访问链接
            val request = Request.Builder().url(baseUrl)
                .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36"
                ).build()


            NetworkUtils.HttpClient.client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext emptyList()
                }
                val responseBody = response.body?.string()
                    ?: throw IllegalArgumentException("Response Body is Null")


                when (region) {
                    Region.DOMESTIC -> {
                        val result = json.decodeFromString<DomesticFontModel>(responseBody)
                        result.apiData.cards.flatMap { card ->
                            card.products.map { product ->
                                SearchFontViewModel.ProductData(
                                    name = product.name,
                                    uuid = product.uuid,
                                    imageUrl = product.imageUrl
                                )
                            }
                        }

                    }

                    Region.INTERNATIONAL -> {
                        val result = json.decodeFromString<InternationFontModel>(responseBody)
                        result.apiData.cards.flatMap { card ->
                            card.products?.map { product ->
                                SearchFontViewModel.ProductData(
                                    name = product.name,
                                    uuid = product.uuid,
                                    imageUrl = product.imageUrl
                                )
                            } ?: emptyList()
                        }
                    }

                }
            }

        }
    }
}