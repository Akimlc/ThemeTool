package xyz.akimlc.themetool.repository.theme

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.akimlc.themetool.data.model.Response
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel


class SearchThemeRepository {
    object HttpClient {
        val client = OkHttpClient()
    }
    suspend fun searchTheme(keywords: String, viewModel: SearchThemeViewModel,context: Context) {

        withContext(Dispatchers.IO) {
            val url =
                "https://api.zhuti.xiaomi.com/app/v9/uipages/search/THEME/index?leftPrice=0&keywords=${keywords}&miuiUIVersion=V150&region=CN"
            val request = Request.Builder().url(url).build()

            try {
                HttpClient.client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("请求失败: ${response.code}")
                    }

                    val jsonString = response.body?.string()
                        ?: throw IllegalArgumentException("Response Body is Null")


                    // 解析 JSON
                    val result =
                        Json { ignoreUnknownKeys = true }.decodeFromString<Response.BaseResponse<Response.DomesticThemeApiData>>(
                            jsonString
                        )
                    val productDataList = result.apiData.cards.flatMap { card ->
                        card.products?.map { product ->
                            SearchThemeViewModel.ProductData(
                                name = product.name,
                                imageUrl = product.imageUrl,
                                uuid = product.uuid,
                                author = product.author,
                                themeSize = product.fileSize
                            )
                        } ?: emptyList()    //如果products = null，则返回空列表
                    }
                    // 更新 ViewModel 中的产品列表
                    withContext(Dispatchers.Main) {
                        if (productDataList.isEmpty()) {
                            Toast.makeText(context, "未找到相关主题", Toast.LENGTH_SHORT).show()
                        }
                        viewModel.updateProductList(productDataList)
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchPage", "搜索失败", e)
            }
        }
    }
}