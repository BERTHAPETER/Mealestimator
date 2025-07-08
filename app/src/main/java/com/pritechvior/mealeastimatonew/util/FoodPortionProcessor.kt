package com.pritechvior.mealeastimatonew.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pritechvior.mealeastimatonew.data.model.CohereRequest
import com.pritechvior.mealeastimatonew.data.network.CohereApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

sealed class PortionResult {
    data class RealData(val grams: Double) : PortionResult()
    data class AIRecommendation(val grams: Double, val aiText: String) : PortionResult()
    object NotAvailable : PortionResult()
}

data class FoodConsumption(
    val ageGroup: String,
    val foodGroup: String,
    val intakeGPerDay: Double,
    val energyKcalPerDay: Double,
    val proteinGPerDay: Double,
    val fatGPerDay: Double
)

object FoodPortionProcessor {
    private var cachedData: List<FoodConsumption>? = null

    fun loadFoodConsumptionData(context: Context): List<FoodConsumption> {
        if (cachedData != null) return cachedData!!
        val assetManager = context.assets
        val inputStream = assetManager.open("tableConvert.com_ojnvsz.json")
        val reader = InputStreamReader(inputStream)
        val rawListType = object : TypeToken<List<Map<String, String>>>() {}.type
        val rawList: List<Map<String, String>> = Gson().fromJson(reader, rawListType)
        cachedData = rawList.mapNotNull { map ->
            try {
                FoodConsumption(
                    ageGroup = map["Age Group"] ?: return@mapNotNull null,
                    foodGroup = map["Food Group"] ?: return@mapNotNull null,
                    intakeGPerDay = map["Intake (g/day)"]?.toDoubleOrNull() ?: 0.0,
                    energyKcalPerDay = map["Energy (kcal/day)"]?.toDoubleOrNull() ?: 0.0,
                    proteinGPerDay = map["Protein (g/day)"]?.toDoubleOrNull() ?: 0.0,
                    fatGPerDay = map["Fat (g/day)"]?.toDoubleOrNull() ?: 0.0
                )
            } catch (e: Exception) {
                null
            }
        }
        return cachedData!!
    }

    fun calculatePortion(
        context: Context,
        ingredient: String,
        ageGroup: String,
        numberOfPeople: Int
    ): PortionResult {
        val data = loadFoodConsumptionData(context)
        val record = data.find {
            it.foodGroup.equals(ingredient, ignoreCase = true) &&
            it.ageGroup.equals(ageGroup, ignoreCase = true)
        }
        return if (record != null) {
            PortionResult.RealData(record.intakeGPerDay * numberOfPeople)
        } else {
            PortionResult.NotAvailable
        }
    }

    suspend fun calculatePortionWithAIFallback(
        context: Context,
        ingredient: String,
        ageGroup: String,
        numberOfPeople: Int,
        cohereApiService: CohereApiService,
        apiKey: String
    ): PortionResult = withContext(Dispatchers.IO) {
        val localResult = calculatePortion(context, ingredient, ageGroup, numberOfPeople)
        if (localResult is PortionResult.RealData) return@withContext localResult

        // Not found, ask AI
        val prompt = "As a Tanzanian food expert, recommend the daily intake (in grams) of $ingredient for $ageGroup."
        val request = CohereRequest(
            model = "command",
            prompt = prompt,
            maxTokens = 50,
            temperature = 0.7,
            k = 0,
            stopSequences = emptyList(),
            returnLikelihoods = "NONE"
        )
        return@withContext try {
            val response = cohereApiService.generatePrediction("Bearer $apiKey", request)
            val aiText = response.generations.firstOrNull()?.text ?: ""
            val grams = Regex("\\d+(?:\\.\\d+)?").find(aiText)?.value?.toDoubleOrNull() ?: 0.0
            PortionResult.AIRecommendation(grams * numberOfPeople, aiText)
        } catch (e: Exception) {
            PortionResult.NotAvailable
        }
    }
} 