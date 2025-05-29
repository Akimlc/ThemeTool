package xyz.akimlc.themetool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.repository.font.SearchFontRepository
import xyz.akimlc.themetool.ui.page.font.Region

class SearchFontViewModel : ViewModel() {
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    val productList: StateFlow<List<ProductData>> = _productList.asStateFlow()

    private val _currentKeyword = MutableStateFlow("")
    val currentKeyword: StateFlow<String> = _currentKeyword.asStateFlow()

    private val _selectedRegion = MutableStateFlow(Region.DOMESTIC)
    val selectedRegion: StateFlow<Region> = _selectedRegion.asStateFlow()

    private val _hasMore = MutableStateFlow(true)

    private var currentPage = 0
    private var currentKeywords = ""
    private var currentVersion = ""
    private var currentRegion = Region.DOMESTIC

    data class ProductData(
        val name: String,
        val imageUrl: String,
        val uuid: String
    )

    fun searchFont(
        region: Region,
        keywords: String,
        version: String,
        page: Int
    ) {
        if (keywords.isEmpty()) return

        currentPage = page
        currentKeywords = keywords
        currentRegion = region
        currentVersion = version

        _currentKeyword.value = keywords

        viewModelScope.launch {
            _isSearching.value = true

            try {
                val result = SearchFontRepository().searchFont(region, keywords, version, page)
                _hasMore.value = result.isNotEmpty()
                if (page==0) {
                    _productList.value = result
                } else {
                    _productList.value = _productList.value + result
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _hasMore.value = false
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun loadMore() {
        if (_isSearching.value || !_hasMore.value) return
        currentPage++
        searchFont(currentRegion, currentKeywords, currentVersion, currentPage)
    }

    fun clearSearchResults() {
        _productList.value = emptyList()
        currentPage = 0
    }

    fun setSelectedRegion(region: Region) {
        _selectedRegion.value = region
    }
}