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
    private val repository = DesignerRepository()

    // 设计师信息
    private val _designerInfo = MutableStateFlow<DesignerInfoData?>(null)
    val designerInfo: StateFlow<DesignerInfoData?> = _designerInfo

    // 设计师作品列表
    private val _designerProductsList = MutableStateFlow<List<DesignerProductData>>(emptyList())
    val designerProducts: StateFlow<List<DesignerProductData>> = _designerProductsList

    // 是否加载更多
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 是否还有更多
    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore


    data class DesignerInfoData(
        val designerIcon: String,
        val name: String,
        val productCount: Int,
        val fansCount: Int
    )

    data class DesignerProductData(
        val name: String,
        val imageUrl: String,
        val uuid: String
    )

    private var currentPage = 0
    private var currentDesignerId = ""

    /** 加载设计师信息 */
    fun loadDesignerInfoData(designerId: String) {
        viewModelScope.launch {
            try {
                val info = repository.getDesignerInfo(designerId)
                info?.let {
                    _designerInfo.value = DesignerInfoData(
                        designerIcon = it.designerIcon,
                        name = it.designerName,
                        productCount = it.productCount,
                        fansCount = it.fansCount
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadDesignerInfoData error: ${e.message}", e)
            }
        }
    }

//    /** 加载第一页作品（刷新页面） */
//    fun loadDesignerPage(designerId: String) {
//        currentPage = 0
//        _designerProducts.value = emptyList()
//        _hasMore.value = true
//        loadMoreProduct(designerId, reset = true)
//    }
//
//    /** 分页加载作品 */
//    fun loadMoreProduct(designerId: String, reset: Boolean = false) {
//        if (_isLoading.value || !_hasMore.value) return
//        viewModelScope.launch {
//            try {
//                val products = repository.getDesignerProduct(designerId, currentPage)
//
//                val mapped = products.apiData.Font.map { product ->
//                    DesignerProductData(
//                        name = product.name,
//                        imageUrl = product.downloadUrlRoot + product.frontCover,
//                        uuid = product.moduleId
//                    )
//                }
//                mapped.forEach {
//                    Log.d(
//                        TAG,
//                        "Product: ${it.name}, UUID: ${it.uuid}, URL: ${it.imageUrl}"
//                    )
//                }
//                _designerProducts.value =
//                    if (reset) mapped else _designerProducts.value + mapped
//
//                _hasMore.value = !products.apiData.isEnding
//
//                if (_hasMore.value) currentPage++
//            } catch (e: Exception) {
//                Log.e(TAG, "loadMoreProduct error: ${e.message}", e)
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }


    // 加载作品数据
    fun designerProducts(designerId: String, page: Int) {
        currentPage = page
        currentDesignerId = designerId
        _hasMore.value = true
        viewModelScope.launch {

            try {
                // 根据 page 设置不同的 loading flag
                _isLoading.value = true
                val products = repository.getDesignerProduct(designerId, currentPage)
                _hasMore.value = products.isNotEmpty()
                if (page==0) {
                    _designerProductsList.value = products
                } else {
                    _designerProductsList.value = _designerProductsList.value + products
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _hasMore.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) return
        currentPage++
        designerProducts(currentDesignerId,currentPage)
    }

}