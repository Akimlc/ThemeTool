package xyz.akimlc.themetool.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.akimlc.themetool.repository.font.DesignerRepository

class DesignerViewModel : ViewModel() {

    private val TAG = "DesignerViewModel"

    // 设计师信息（用于 UI 展示）
    private val _designerInfo = MutableStateFlow<designerInfoData?>(null)
    val designerInfo: StateFlow<designerInfoData?> = _designerInfo

    // 设计师作品列表（用于 UI 展示）
    private val _designerProducts = MutableStateFlow<List<designerProductData>>(emptyList())
    val designerProducts: StateFlow<List<designerProductData>> = _designerProducts

    //进度条
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    data class designerInfoData(
        val designerIcon: String,
        val name: String,
        val productCount: Int,
        val fansCount: Int
    )

    data class designerProductData(
        val name: String,
        val imageUrl: String,
        val uuid: String
    )

    fun loadDesignerInfoData(designerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val info = DesignerRepository().getDesignerInfo(designerId)
            info?.let {
                _designerInfo.value = designerInfoData(
                    designerIcon = it.designerIcon,
                    name = it.designerName,
                    productCount = it.productCount,
                    fansCount = it.fansCount
                )
            }
            _isLoading.value = false
        }
    }

    fun loadDesignerProductData(designerId: String) {
        val repository = DesignerRepository()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val products = repository.getDesignerProduct(designerId)
                _designerProducts.value = products.apiData.Font.map { product ->
                    designerProductData(
                        name = product.name,
                        imageUrl = product.downloadUrlRoot + product.frontCover,
                        uuid = product.moduleId
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadDesignerProductData error: ${e.message}", e)
                _designerProducts.value = emptyList()
            }finally {
                _isLoading.value = false
            }
        }
    }
}
