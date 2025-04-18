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
import xyz.akimlc.themetool.repository.ThemeRepository
import xyz.akimlc.themetool.repository.theme.SearchThemeRepository

class SearchThemeViewModel : ViewModel() {
    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    val productList: StateFlow<List<ProductData>> get() = _productList
    val themeInfoState = mutableStateOf<ThemeInfo?>(null)

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    data class ProductData(
        val name: String,
        val author: String?,
        val themeSize: Int?,
        val imageUrl: String,
        val uuid: String
    )

    // 搜索主题
    fun searchTheme(keywords: String, onEmpty: () -> Unit = {}) {
        viewModelScope.launch {
            _isSearching.value = true // 开始搜索
            try {
                val result = SearchThemeRepository.searchTheme(keywords)
                _productList.value = result
                if (result.isEmpty()) onEmpty()
            } catch (e: Exception) {
                Log.e("SearchTheme", "搜索失败", e)
            } finally {
                _isSearching.value = false // 结束搜索
            }
        }
    }

    // 解析主题详情
    fun parseTheme(uuid: String) {
        viewModelScope.launch {
            try {
                val themeInfo = ThemeRepository().parseTheme(uuid)
                themeInfoState.value = themeInfo
            } catch (e: Exception) {
                Log.e("SearchTheme", "解析失败", e)
                themeInfoState.value = null
            }
        }
    }
}
