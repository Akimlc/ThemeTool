package xyz.akimlc.themetool.data.model

import kotlinx.serialization.Serializable

class Response {
    @Serializable
    data class BaseResponse<T>(
        val apiCode: Int,
        val apiMessage: String,
        val apiData: T
    )



    @Serializable
    data class ProductData(
        val uuid: String,
        val name: String,
        val imageUrl: String,
        val author: String? = null,
        val fileSize: Int? = null
    )

    // GlobalFont 专属数据
    @Serializable
    data class GlobalFontApiData(
        val cards: List<Card>,
        val hasMore: Boolean
    )

    @Serializable
    data class GlobalFontCard(
        val products: List<ProductData>
    )


    // 国内主题的APi响应
    @Serializable
    data class DomesticThemeApiData(
        val id: Int,
        val cards: List<Card>,
        val online: Boolean,
        val fileServer: String,
        val hasMore: Boolean
    )

    @Serializable
    data class Card(
        val products: List<ProductData> ?= null
    )
}