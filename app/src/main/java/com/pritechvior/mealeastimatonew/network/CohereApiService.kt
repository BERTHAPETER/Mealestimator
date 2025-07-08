package com.pritechvior.mealeastimatonew.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import com.google.gson.annotations.SerializedName

// Data classes for Cohere API

data class CohereRequest(
    @SerializedName("model") val model: String = "command",
    @SerializedName("prompt") val prompt: String,
    @SerializedName("max_tokens") val maxTokens: Int = 100
)

data class CohereResponse(
    @SerializedName("generations") val generations: List<CohereGeneration>
)

data class CohereGeneration(
    @SerializedName("text") val text: String
)

interface CohereApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/generate")
    suspend fun generate(@Body request: CohereRequest): CohereResponse

    companion object {
        fun create(apiKey: String): CohereApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $apiKey")
                        .build()
                    chain.proceed(request)
                }
                .build()
            return Retrofit.Builder()
                .baseUrl("https://api.cohere.ai/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CohereApiService::class.java)
        }
    }
} 