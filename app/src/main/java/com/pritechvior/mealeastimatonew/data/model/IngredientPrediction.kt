package com.pritechvior.mealeastimatonew.data.model

data class IngredientPredictionRequest(
    val foodType: String,
    val ingredient: String,
    val numberOfPeople: Int,
    val ageGroups: List<String>
)

data class IngredientPrediction(
    val ingredient: String,
    val quantity: Double,
    val unit: String,
    val energyKcal: Double = 0.0,
    val proteinGrams: Double = 0.0,
    val fatGrams: Double = 0.0
) 