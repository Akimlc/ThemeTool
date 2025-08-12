package xyz.akimlc.themetool.data.model.response.font

import kotlinx.serialization.Serializable

@Serializable
data class GlobalFontResponse(
    val apiCode: Int,
    val apiMessage: String,
    val apiData: ApiData
)

@Serializable
data class ApiData(
    val cards: List<Cards>,
    val hasMore: Boolean,
)

@Serializable
data class Cards(
    val products: List<Products>? = null,
    val cardType : String
)

@Serializable
data class Products(
    val name: String,
    val uuid: String,
    val imageUrl: String,
)