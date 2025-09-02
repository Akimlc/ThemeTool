package xyz.akimlc.themetool.repository.font

import android.util.Log
import xyz.akimlc.themetool.data.model.response.font.DesignerInfoResponseData
import xyz.akimlc.themetool.data.model.response.font.DesignerProductResponse
import xyz.akimlc.themetool.utils.NetworkUtils

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


    suspend fun getDesignerProduct(designerId: String, page: Int = 0): DesignerProductResponse {
        return NetworkUtils().designerProductApi.getDesignerProduct(designerId, page)
    }
}