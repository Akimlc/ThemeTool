package xyz.akimlc.themetool.data.model.response.font

import kotlinx.serialization.Serializable


@Serializable
data class DesignerProductResponse(
    val apiCode: Int,
    val apiData: DesignerProductResponseData
)

@Serializable
data class DesignerProductResponseData(
    val Font: List<Font>,
    val isEnding: Boolean
)

@Serializable
data class Font(
    val assemblyId: String,
    val disCent: Int,
    val disPer: Int,
    val downloadUrlRoot: String,
    val downloads: Int,
    val fileSize: Int,
    val frontCover: String,
    val moduleId: String,
    val moduleType: String,
    val name: String,
    val originPrice: Int,
    val playTime: Int,
    val position: Int,
    val productBought: Boolean,
    val productPrice: Int,
    val publisherPrincipalId: Int,
    val score: Double,
    val source: String,
    val stockstate: Int,
    val version: Int
)