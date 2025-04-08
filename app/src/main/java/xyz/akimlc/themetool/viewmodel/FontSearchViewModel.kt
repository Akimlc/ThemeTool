package xyz.akimlc.themetool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.data.model.Info.FontInfo
import xyz.akimlc.themetool.repository.ThemeRepository

class FontSearchViewModel : ViewModel() {
    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    val productList: StateFlow<List<ProductData>> get() = _productList
    val fontInfoState = MutableStateFlow<FontInfo?>(null)

    fun updateProductList(products: List<ProductData>) {
        _productList.value = products
    }

    data class ProductData(
        val name: String,
        val imageUrl: String,
        val uuid: String
    )

    fun parseFont(uuid: String) {
        viewModelScope.launch{
            try {
                val fontInfo = ThemeRepository().parseFont(uuid)
                fontInfoState.value = fontInfo // 这里更新数据
            } catch (e: Exception) {
                e.printStackTrace()
                fontInfoState.value = null
            }
        }
    }


}