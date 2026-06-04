package com.example.clipscore.data.remote

import com.example.clipscore.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val BASE_URL = if (BuildConfig.DEBUG) {
        "http://10.0.2.2:5000/"
    } else {
        BuildConfig.BACKEND_URL
    }

    private val gson: Gson = GsonBuilder().create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: BackendApi = retrofit.create(BackendApi::class.java)
}
