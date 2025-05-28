package xyz.akimlc.themetool.data.model.font

import kotlinx.serialization.Serializable

@Serializable
data class DomesticFontModel(
    val apiCode: Int,
    val apiMessage: String,
    val apiData: DomesticApiData
)


@Serializable
data class DomesticApiData(
    val cards: List<DomesticApiCards>,
    val fileServer: String,
    val hasMore: Boolean
)

@Serializable
data class DomesticApiCards(
    val products: List<DomesticApiProducts>,
    val hasMore: Boolean,
)

@Serializable
data class DomesticApiProducts(
    val uuid: String,
    val name: String,
    val imageUrl: String,
    val fileSizeInKB: Long,
    val authorName: String
)
