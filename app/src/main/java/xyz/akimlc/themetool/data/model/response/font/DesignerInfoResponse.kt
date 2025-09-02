package xyz.akimlc.themetool.data.model.response.font

import kotlinx.serialization.Serializable

@Serializable
data class DesignerInfoResponse(
    val apiData: DesignerInfoResponseData,
    val apiMessage: String,
    val apiCode: Int,
)

@Serializable
data class DesignerInfoResponseData(
    val designerIcon: String,
    val designerId: Int,
    val designerMiId: Long,
    val designerName: String,
    val fansCount: Int,
    val hasFollow: Boolean,
    val popularity: Int,
    val productCount: Int,
    val viewCount: Int
)