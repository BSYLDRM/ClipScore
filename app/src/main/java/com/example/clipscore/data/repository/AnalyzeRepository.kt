package com.example.clipscore.data.repository

import com.example.clipscore.data.model.AnalyzeRequest
import com.example.clipscore.data.model.AnalyzeResponse
import com.example.clipscore.data.remote.BackendApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyzeRepository @Inject constructor(
    private val api: BackendApi,
) {
    suspend fun analyze(request: AnalyzeRequest): Result<AnalyzeResponse> = runCatching {
        api.analyze(request)
    }
}
