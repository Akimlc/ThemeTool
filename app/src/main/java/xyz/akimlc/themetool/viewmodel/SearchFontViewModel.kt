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

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _hasMore = MutableStateFlow(true) // 标记是否还有更多数据

    val fontInfoState = MutableStateFlow<FontInfo?>(null)

    private var currentPage = 1
    private var currentKeyword = ""

    fun updateProductList(products: List<ProductData>) {
        _productList.value = products
    }

    data class ProductData(
        val name: String,
        val imageUrl: String,
        val uuid: String
    )

    // 搜索字体，初次搜索时重置页码
    fun searchFont(keyword: String, onEmpty: () -> Unit = {}) {
        currentKeyword = keyword
        currentPage = 0 // 每次新搜索时重置页数
        loadFonts(keyword, onEmpty)
    }

    // 加载字体数据
    fun loadFonts(keyword: String, onEmpty: () -> Unit = {}) {
        if (_isSearching.value) return // 如果正在加载，避免重复请求

        _isSearching.value = true
        viewModelScope.launch {
            try {
                val result = SearchFontRepository().searchFont(keyword, currentPage)
                // 如果是第一页数据，直接替换列表，否则追加到现有数据
                if (currentPage==0) {
                    _productList.value = result
                } else {
                    _productList.value += result
                }

                // 判断是否有更多数据
                _hasMore.value = result.isNotEmpty()

                if (result.isEmpty()) onEmpty()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearching.value = false
            }
        }
    }

    // 加载更多数据
    fun loadMoreFont() {
        if (_isSearching.value || !_hasMore.value) return // 如果正在加载或没有更多数据，直接返回
        currentPage++
        loadFonts(currentKeyword)
    }

    fun parseFont(uuid: String) {
        viewModelScope.launch {
            try {
                val fontInfo = ThemeRepository().parseFont(uuid)
                fontInfoState.value = fontInfo
            } catch (e: Exception) {
                e.printStackTrace()
                fontInfoState.value = null
            }
        }
    }
}