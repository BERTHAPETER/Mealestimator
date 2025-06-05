package com.pritechvior.mealeastimatonew.data.repository

import android.util.Log
import com.pritechvior.mealeastimatonew.data.model.IngredientPrediction
import com.pritechvior.mealeastimatonew.data.model.IngredientPredictionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "IngredientPrediction"

class IngredientPredictionRepository {
    
    suspend fun predictIngredient(request: IngredientPredictionRequest): Result<IngredientPrediction> =
        withContext(Dispatchers.Default) {
            try {
                Log.d(TAG, "Starting prediction for ${request.ingredient}")
                
                // Basic validation
                if (request.foodType.isBlank() || request.ingredient.isBlank() || request.numberOfPeople <= 0) {
                    throw IllegalArgumentException("Invalid request parameters")
                }

                // Calculate base quantity per person based on ingredient and meal type
                val (baseQuantity, unit) = when (request.ingredient.lowercase()) {
                    "rice" -> when (request.foodType.lowercase()) {
                        "breakfast" -> 0.5 to "cups"
                        "lunch", "dinner" -> 1.0 to "cups"
                        else -> 0.75 to "cups"
                    }
                    "beef" -> when (request.foodType.lowercase()) {
                        "breakfast" -> 100.0 to "grams"
                        "lunch", "dinner" -> 200.0 to "grams"
                        else -> 150.0 to "grams"
                    }
                    "coconut milk" -> 0.5 to "cups"
                    "onions" -> 0.25 to "pieces"
                    "garlic" -> 1.0 to "cloves"
                    "ginger" -> 0.5 to "teaspoons"
                    "tomatoes" -> 0.5 to "pieces"
                    "pilau masala" -> 1.0 to "teaspoons"
                    "salt" -> 0.25 to "teaspoons"
                    "black pepper" -> 0.25 to "teaspoons"
                    else -> 1.0 to "units" // Default for unknown ingredients
                }

                // Adjust quantity based on age groups
                var totalQuantity = baseQuantity * request.numberOfPeople
                request.ageGroups.forEach { ageGroup ->
                    val multiplier = when (ageGroup.lowercase()) {
                        "child" -> 0.5
                        "teen" -> 1.2
                        "adult" -> 1.0
                        "elderly" -> 0.8
                        else -> 1.0
                    }
                    totalQuantity *= multiplier
                }

                val prediction = IngredientPrediction(
                    ingredient = request.ingredient,
                    quantity = totalQuantity,
                    unit = unit
                )

                Log.d(TAG, "Successfully calculated prediction: $prediction")
                Result.success(prediction)
            } catch (e: Exception) {
                Log.e(TAG, "Error during prediction calculation", e)
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