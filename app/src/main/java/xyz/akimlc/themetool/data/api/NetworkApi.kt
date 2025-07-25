package xyz.akimlc.themetool.data.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.akimlc.themetool.data.model.GlobalFontResponse
import xyz.akimlc.themetool.data.model.response.theme.SearchDomesticThemeResponse
import xyz.akimlc.themetool.data.model.response.theme.SearchGlobalThemeResponse

interface NetworkApi {
    // 国际字体
    @GET("app/v9/uipages/font/{uuid}")
    suspend fun getGlobalFont(
        @Path("uuid") uuid: String,
        @Query("isGlobal") isGlobal: Boolean = true,
        @Query("language") language: String = "zh_CN",
        @Query("devicePixel") devicePixel: Int = 1080,
        @Query("device") device: String = "apollo",
        @Query("region") region: String = "MC"
    ): GlobalFontResponse


    //
    @GET("app/v9/uipages/search/THEME/index")
    suspend fun searchDomesticTheme(
        @Query("leftPrice") leftPrice: Int = 0,
        @Query("keywords") keywords: String,
        @Query("miuiUIVersion") miuiUIVersion: String = "V150",
        @Query("region") region: String = "CN",
        @Query("cardStart") cardStart: Int
    ): SearchDomesticThemeResponse

    @GET("thm/search/v2/npage")
    suspend fun searchGlobalTheme(
        @Query("category") category: String = "Compound",
        @Query("keywords") keywords: String,
        @Query("miuiUIVersion") miuiUIVersion: String = "V160",
        @Query("page") page: Int,
        @Query("language") language: String = "zh_CN",
        @Query("device") device: String = "d",
        @Query("region") region: String = "MC",
        @Query("isGlobal") isGlobal: Boolean = true,
        @Header("User-Agent") userAgent: String = "Mozilla/5.0 (Linux; Android 12; M2007J3SC Build/SP1A.210812.016; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/86.0.4240.99 XWEB/4275 MMWEBSDK/20220204 Mobile Safari/537.36 MMWEBID/4275 MicroMessenger/8.0.20.2240(0x2800143C)WeChat/arm64 Weixin NetType/WIFI Language/zh"
    ): SearchGlobalThemeResponse
}