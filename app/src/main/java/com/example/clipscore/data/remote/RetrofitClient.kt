package com.example.clipscore.data.remote

import com.example.clipscore.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://clipscore-dmmb.onrender.com/"

    private val gson: Gson = GsonBuilder().create()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            var response = chain.proceed(request)
            var tryCount = 0
            // 502 veya 503 alınca 2 kez daha dene
            while ((response.code() == 502 || response.code() == 503) && tryCount < 2) {
                tryCount++
                response.close()
                Thread.sleep(2000L * tryCount) // 2sn, 4sn bekle
                response = chain.proceed(request)
            }
            response
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: BackendApi = retrofit.create(BackendApi::class.java)
}
