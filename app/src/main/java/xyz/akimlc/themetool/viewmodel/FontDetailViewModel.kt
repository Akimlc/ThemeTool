package xyz.akimlc.themetool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.data.model.FontDetail
import xyz.akimlc.themetool.repository.ThemeRepository

class FontDetailViewModel : ViewModel() {
    private val _fontDetail = MutableStateFlow<FontDetail?>(null)
    val fontDetail: StateFlow<FontDetail?> = _fontDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadFontData(uuid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val fontInfo = ThemeRepository().parseFont(uuid)
                _fontDetail.value = fontInfo
            } catch (e: Exception) {
                _errorMessage.value = "加载字体数据失败"
            } finally {
                _isLoading.value = false
            }
        }
    }
}