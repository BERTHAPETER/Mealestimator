package com.pritechvior.mealeastimatonew.data.repository

import android.util.Log
import com.pritechvior.mealeastimatonew.data.model.IngredientPrediction
import com.pritechvior.mealeastimatonew.data.model.IngredientPredictionRequest
import com.pritechvior.mealeastimatonew.util.PortionCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "IngredientPrediction"

class IngredientPredictionRepository {
    
    suspend fun predictIngredient(request: IngredientPredictionRequest): Result<IngredientPrediction> {
        return try {
            // Calculate quantity and unit based on real Tanzanian data
            val (quantity, unit) = PortionCalculator.calculateIngredientQuantity(
                ingredient = request.ingredient,
                numberOfPeople = request.numberOfPeople,
                ageGroups = request.ageGroups
            )

            // Calculate nutrition information
            val (energy, protein, fat) = PortionCalculator.calculateNutrition(
                ingredient = request.ingredient,
                quantity = quantity,
                unit = unit,
                ageGroups = request.ageGroups
            )

            Result.success(
                IngredientPrediction(
                    ingredient = request.ingredient,
                    quantity = quantity,
                    unit = unit,
                    energyKcal = energy,
                    proteinGrams = protein,
                    fatGrams = fat
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSupportedIngredients(): Set<String> = setOf(
        "Rice",
        "Beef",
        "Coconut Milk",
        "Onions",
        "Garlic",
        "Ginger",
        "Tomatoes",
        "Pilau Masala",
        "Salt",
        "Black Pepper"
    )
} 