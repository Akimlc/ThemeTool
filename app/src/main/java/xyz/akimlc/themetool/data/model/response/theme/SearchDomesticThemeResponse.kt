package xyz.akimlc.themetool.data.model.response.theme

import kotlinx.serialization.Serializable

@Serializable
data class SearchDomesticThemeResponse(
    val apiCode: Int,
    val apiMessage: String,
    val apiData: DomesticThemeApiData
)

@Serializable
data class DomesticThemeApiData(
    val id: Int?,
    val cards: List<Card>,
    val online: Boolean,
    val fileServer: String,
    val hasMore: Boolean
)

@Serializable
data class Card(
    val products: List<ProductData>? = null,
    val cardType: String
)

@Serializable
data class ProductData(
    val uuid: String,
    val name: String,
    val imageUrl: String,
    val author: String? = null,
    val fileSize: Int? = null
)
