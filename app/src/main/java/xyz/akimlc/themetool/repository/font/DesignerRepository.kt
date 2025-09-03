package xyz.akimlc.themetool.repository.font

import android.util.Log
import xyz.akimlc.themetool.data.model.response.font.DesignerInfoResponseData
import xyz.akimlc.themetool.utils.NetworkUtils
import xyz.akimlc.themetool.viewmodel.DesignerViewModel

class DesignerRepository {

    private final val TAG = "DesignerRepository"
    suspend fun getDesignerInfo(designerId: String): DesignerInfoResponseData? {
        return try {
            val response = NetworkUtils().designerInfoApi.getDesignerInfo(designerId)
            if (response.apiCode==0) {
                response.apiData
            } else null

        } catch (e: Exception) {
            Log.e(TAG, "获取设计师信息异常: $e")
            null
        }
    }
    suspend fun getDesignerProduct(
        designerId: String,
        page: Int
    ): List<DesignerViewModel.DesignerProductData> {
        val response = NetworkUtils().designerProductApi.getDesignerProduct(designerId, page)
        Log.d(TAG, "getDesignerProduct: $response")
        Log.d(TAG, "getDesignerProduct: $designerId")

        return try {
            if (response.apiCode != 0) {
                Log.e("DesignerRepository", "接口返回错误: ${response.apiData}")
                return emptyList()
            }
            response.apiData.Font.map {
                DesignerViewModel.DesignerProductData(
                    name = it.name,
                    imageUrl = it.downloadUrlRoot + it.frontCover,
                    uuid = it.moduleId
                )
            }
        }catch (e: Exception){
            Log.e("DesignerRepository", "getDesignerProduct error: ${e.message}", e)
            emptyList()
        }
    }
}