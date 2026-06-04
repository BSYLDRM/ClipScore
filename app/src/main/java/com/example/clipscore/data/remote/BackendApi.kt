package com.example.clipscore.data.remote

import com.example.clipscore.data.model.AnalyzeRequest
import com.example.clipscore.data.model.AnalyzeResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendApi {
    @POST("api/analyze")
    suspend fun analyze(@Body request: AnalyzeRequest): AnalyzeResponse
}
