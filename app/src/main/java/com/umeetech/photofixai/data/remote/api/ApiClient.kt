package com.umeetech.photofixai.data.remote.api

import com.umeetech.photofixai.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds Retrofit services for the production API mode.
 *
 * The base URL comes from BuildConfig.BACKEND_BASE_URL (injected in Gradle), so no
 * endpoint or secret is hardcoded in source. Logging is verbose only in debug.
 */
object ApiClient {

    private fun okHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .client(okHttp())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val backgroundRemovalApi: BackgroundRemovalApi by lazy {
        retrofit.create(BackgroundRemovalApi::class.java)
    }
}
