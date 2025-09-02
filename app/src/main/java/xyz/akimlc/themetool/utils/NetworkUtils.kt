package xyz.akimlc.themetool.utils

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import xyz.akimlc.themetool.data.api.NetworkApi
import java.util.concurrent.TimeUnit

class NetworkUtils {

    object HttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true) // 断开重试
            .build()
    }

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36"
                    )
                    .build()
                chain.proceed(request)
            }
            .build()
    }


    private val domesticRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.zhuti.xiaomi.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private val globalRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://thm.market.intl.xiaomi.com")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private val designerProductRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://thm.market.intl.xiaomi.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private val designerInfoRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.zhuti.intl.xiaomi.com")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }


    val domesticApi: NetworkApi by lazy {
        domesticRetrofit.create(NetworkApi::class.java)
    }

    val globalApi: NetworkApi by lazy {
        globalRetrofit.create(NetworkApi::class.java)
    }

    val designerProductApi by lazy {
        designerProductRetrofit.create(NetworkApi::class.java)
    }
    val designerInfoApi by lazy {
        designerInfoRetrofit.create(NetworkApi::class.java)
    }
}
