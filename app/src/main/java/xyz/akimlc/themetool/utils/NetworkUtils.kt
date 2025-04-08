package xyz.akimlc.themetool.utils

import okhttp3.OkHttpClient

class NetworkUtils {
    object HttpClient {
        val client = OkHttpClient()
    }
}