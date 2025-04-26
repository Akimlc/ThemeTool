package xyz.akimlc.themetool.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GlobalFontResponse(
    val apiCode: String,
    val apiMessage: String,
    val apiData: ApiData
)

@Serializable
data class ApiData(
    val cards: List<Card>,
    val fileService: String,
    val hasMore: Boolean
)

@Serializable
data class Card(
    val extraInfo: ExtraInfo
)

@Serializable
data class ExtraInfo(
    val themeDetail: ThemeDetail,
    val hasRecommend: Boolean
)

@Serializable
data class ThemeDetail(
    val name: String,
    val assemblyId: String,
    val moduleId: String,
    val moduleType: String,
    val downloadUrlRoot: String,
    val description: String,
    val author: String,
    val designer: String,
    val fileSize: Long,
    val version: String,
    val downloads: Int,
    val tags: String,
    val productId: String,
    val downloadUrl: String,
    val productType: String
)
