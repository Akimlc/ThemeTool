package xyz.akimlc.themetool.data.network

import kotlinx.serialization.Serializable

@Serializable
data class GlobalThemeGlobal(
    val apiCode: Int,
    val apiMessage: String,
    val apiData: GlobalThemeData
)

@Serializable
data class GlobalThemeData(
    val hasMore: Boolean,
    val cards: List<GlobalThemeCard>
)


@Serializable
data class GlobalThemeCard(
    val products: List<GlobalThemeProducts>
)

@Serializable
data class GlobalThemeProducts(
    val uuid: String,
    val name: String,
    val imageUrl: String
)