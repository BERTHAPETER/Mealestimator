package com.pritechvior.mealeastimatonew.data.network

import com.pritechvior.mealeastimatonew.data.model.CohereRequest
import com.pritechvior.mealeastimatonew.data.model.CohereResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CohereApiService {
    @POST("v1/generate")
    suspend fun generatePrediction(
        @Header("Authorization") apiKey: String,
        @Body request: CohereRequest
    ): CohereResponse
} 