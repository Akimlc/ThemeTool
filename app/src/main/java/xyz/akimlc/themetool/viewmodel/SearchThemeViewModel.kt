package xyz.akimlc.themetool.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.data.model.Info.ThemeInfo
import xyz.akimlc.themetool.repository.Parse
import xyz.akimlc.themetool.repository.theme.SearchThemeRepository

class SearchThemeViewModel : ViewModel() {
    private val parse = Parse()

    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    private val _globalThemeProductList = MutableStateFlow<List<GlobalProductData>>(emptyList())
    val productList: StateFlow<List<ProductData>> get() = _productList
    val globalThemeProductList: StateFlow<List<GlobalProductData>> get() = _globalThemeProductList
    val themeInfoState = mutableStateOf<ThemeInfo?>(null)

    private val _isSearching = MutableStateFlow(false)
    private val _isSearchingGlobal = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    val isSearchingGlobal: StateFlow<Boolean> = _isSearchingGlobal.asStateFlow()
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _isGlobalLoadingMore = MutableStateFlow(false)
    var isGlobalLoadingMore: StateFlow<Boolean> = _isGlobalLoadingMore


    private var currentPage = 0
    private var currentKeyword = ""
    private var hasMore = true
    private var globalCurrentPage = 0
    private var globalCurrentKeyword = ""
    private var globalThemeHasMore = true

    data class ProductData(
        val name: String,
        val author: String?,
        val themeSize: Int?,
        val imageUrl: String,
        val uuid: String
    )

    data class GlobalProductData(
        val name: String,
        val uuid: String,
        val imageUrl: String
    )

    fun loadMoreGlobalTheme() {
        if (_isGlobalLoadingMore.value || !globalThemeHasMore) return
        viewModelScope.launch {
            _isGlobalLoadingMore.value = true
            try {
                val result = SearchThemeRepository.searchGlobalTheme(globalCurrentKeyword, globalCurrentPage)
                if (result.isNotEmpty()) {
                    _globalThemeProductList.value = _globalThemeProductList.value + result
                    globalCurrentPage++
                } else {
                    globalThemeHasMore = false
                }
            } catch (e: Exception) {
                Log.e("SearchTheme", "国际加载更多失败", e)
            } finally {
                _isGlobalLoadingMore.value = false
            }
        }
    }

    fun loadMoreTheme() {
        if (_isLoadingMore.value || !hasMore) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val result = SearchThemeRepository.searchDomesticTheme(currentKeyword, currentPage)
                if (result.isNotEmpty()) {
                    _productList.value = _productList.value + result
                    currentPage++
                } else {
                    hasMore = false
                }
            } catch (e: Exception) {
                Log.e("SearchTheme", "加载更多失败", e)
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun searchGlobalTheme(keywords: String, onEmpty: () -> Unit) {
        globalCurrentKeyword = keywords
        globalCurrentPage = 0
        globalThemeHasMore = true
        _isSearchingGlobal.value = true
        viewModelScope.launch {
            try {
                val result = SearchThemeRepository.searchGlobalTheme(keywords, globalCurrentPage)
                _globalThemeProductList.value = result
                if (result.isEmpty()){
                    globalThemeHasMore = false
                } else {
                    globalCurrentPage++
                }
            } catch (e: Exception) {
                Log.e("SearchTheme", "国际搜索失败", e)
                onEmpty()
            } finally {
                _isSearchingGlobal.value = false
            }
        }
    }

    fun searchTheme(keywords: String, onEmpty: () -> Unit = {}) {
        currentKeyword = keywords
        currentPage = 0
        hasMore = true
        _isSearching.value = true
        viewModelScope.launch {
            try {

                val result = SearchThemeRepository.searchDomesticTheme(keywords, 0)
                _productList.value = result
                if (result.isEmpty()) onEmpty()
                else currentPage++
            } catch (e: Exception) {
                Log.e("SearchTheme", "国内搜索失败", e)
                onEmpty()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearchResults() {
        _productList.value = emptyList()
        currentPage = 0
    }

    fun clearGlobalThemeResults() {
        _globalThemeProductList.value = emptyList()
        globalCurrentPage = 0
    }

}
