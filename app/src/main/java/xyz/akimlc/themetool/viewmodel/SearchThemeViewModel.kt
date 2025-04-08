package xyz.akimlc.themetool.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.data.model.Info.ThemeInfo
import xyz.akimlc.themetool.repository.ThemeRepository

class SearchThemeViewModel : ViewModel() {
    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    val productList: StateFlow<List<ProductData>> get() = _productList
    val themeInfoState = mutableStateOf<ThemeInfo?>(null)
    // 更新产品列表
    fun updateProductList(products: List<ProductData>) {
        _productList.value = products
    }

    data class ProductData(
        val name: String,
        val author: String?,
        val themeSize: Int?,
        val imageUrl: String,
        val uuid: String
    )

    fun parseTheme(uuid: String) {
        viewModelScope.launch {
            try {
                val themeInfo = ThemeRepository().parseTheme(uuid)
                themeInfoState.value = themeInfo // 这里更新数据
            } catch (e: Exception) {
                e.printStackTrace()
                themeInfoState.value = null
            }
        }
    }
}
