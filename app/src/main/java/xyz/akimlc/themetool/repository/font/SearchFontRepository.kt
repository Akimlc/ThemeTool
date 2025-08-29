package xyz.akimlc.themetool.repository.font

import android.util.Log
import xyz.akimlc.themetool.ui.page.font.Region
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel

class SearchFontRepository {
    private val TAG = "SearchFontRepository"
    suspend fun searchFont(
        region: Region,
        keyword: String,
        page: Int
    ): List<SearchFontViewModel.ProductData> {
        return try {
            when (region) {
                Region.DOMESTIC -> {
                    val result = NetworkUtils().domesticApi.searchDomesticFont(
                        keywords = keyword,
                        cardStart = page
                    )
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

                Region.GLOBAL -> {
                    val result = NetworkUtils().globalApi.searchGlobalFont(
                        keywords = keyword,
                        page = page,
                    )

                    if (result.apiData.cards.any { it.cardType == "SEARCH_EMPTY_CARD" || it.products.isNullOrEmpty() }) {
                        emptyList()
                    } else {
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
        } catch (e: Exception) {
            Log.e("SearchFontRepository", "搜索字体失败", e)
            emptyList()
        }
    }
}
