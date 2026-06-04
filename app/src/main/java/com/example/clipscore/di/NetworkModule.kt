package com.example.clipscore.di

import com.example.clipscore.data.remote.BackendApi
import com.example.clipscore.data.remote.RetrofitClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideBackendApi(): BackendApi = RetrofitClient.api
}
