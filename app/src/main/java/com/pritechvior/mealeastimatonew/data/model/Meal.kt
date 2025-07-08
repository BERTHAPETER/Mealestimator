package com.pritechvior.mealeastimatonew.data.model

data class Meal(
    val id: String,
    val name: String,
    val nameSwahili: String = "", // Swahili name of the dish
    val description: String,
    val imageUrl: String? = null,
    val emoji: String? = null,
    val basePreparationTime: Int, // in minutes
    val difficulty: String,
    val defaultIngredients: List<String> = emptyList() // Default ingredients for the meal
)