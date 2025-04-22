package xyz.akimlc.themetool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.data.model.Info.FontInfo
import xyz.akimlc.themetool.repository.ThemeRepository
import xyz.akimlc.themetool.repository.font.SearchFontRepository

class SearchFontViewModel : ViewModel() {
    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    val productList: StateFlow<List<ProductData>> get() = _productList
    val fontInfoState = MutableStateFlow<FontInfo?>(null)

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun updateProductList(products: List<ProductData>) {
        _productList.value = products
    }

    data class ProductData(
        val name: String,
        val imageUrl: String,
        val uuid: String
    )

    fun searchFont(keyword: String, onEmpty: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isSearching.value = true
                val result = SearchFontRepository().searchFont(keyword)
                _productList.value = result
                if (result.isEmpty()) onEmpty
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearching.value = false
            }

        }

    }

    fun parseFont(uuid: String) {
        viewModelScope.launch {
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