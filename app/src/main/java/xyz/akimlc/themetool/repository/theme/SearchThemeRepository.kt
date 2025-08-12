package xyz.akimlc.themetool.repository.theme

import kotlinx.serialization.json.Json
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel


object SearchThemeRepository {

    val json by lazy {
        Json { ignoreUnknownKeys = true }
    }

    suspend fun searchDomesticTheme(
        keywords: String,
        page: Int
    ): List<SearchThemeViewModel.ProductData> {
        val response = NetworkUtils().domesticApi.searchDomesticTheme(keywords = keywords, cardStart = page)
        return response.apiData.cards.flatMap { card ->
            card.products?.map {
                SearchThemeViewModel.ProductData(
                    name = it.name,
                    imageUrl = it.imageUrl,
                    uuid = it.uuid,
                    author = it.author,
                    themeSize = it.fileSize
                )
            } ?: emptyList()
        }
    }

    suspend fun searchGlobalTheme(
        keywords: String,
        page: Int
    ): List<SearchThemeViewModel.GlobalProductData>{
        val response = NetworkUtils().globalApi.searchGlobalTheme(keywords = keywords, page = page)
        return response.apiData.cards.flatMap { card ->
            card.products.map {
                SearchThemeViewModel.GlobalProductData(
                    name = it.name,
                    imageUrl = it.imageUrl,
                    uuid = it.uuid
                )
            }
        }
    }
}