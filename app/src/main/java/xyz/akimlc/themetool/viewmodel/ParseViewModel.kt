package xyz.akimlc.themetool.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.data.model.Info.ThemeInfo
import xyz.akimlc.themetool.repository.ThemeRepository

class ParseViewModel(
    private val repository: ThemeRepository = ThemeRepository()
) : ViewModel() {

    //获取到的主题状态
    val themeInfoState = mutableStateOf<ThemeInfo?>(null)

    //错误信息
    val errorMessage = mutableStateOf<String?>(null)

    fun parseTheme(shareLink: String) {
        // 正则表达式提取 packId
        val linkPattern = Regex(
            """https://zhuti\.xiaomi\.com/detail/share/[a-zA-Z0-9\-]+\?miref=share&packId=([a-zA-Z0-9\-]+)"""
        )
        val matchResult = linkPattern.find(shareLink)
        if (matchResult!=null) {
            val packId = matchResult.groupValues[1]
            if (packId.length!=36) {
                errorMessage.value = "解析失败，请检查链接"
                return
            }
            viewModelScope.launch {
                try {
                    val themeInfo = repository.parseTheme(packId)
                    if (themeInfo!=null) {
                        themeInfoState.value = themeInfo
                    } else {
                        errorMessage.value = "解析失败，未获得主题信息"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    errorMessage.value = "解析过程中出错: ${e.message}"
                }
            }
        } else {
            errorMessage.value = "链接格式错误"
        }
    }

    /** 清除错误信息 */
    fun clearError() {
        errorMessage.value = null
    }
}