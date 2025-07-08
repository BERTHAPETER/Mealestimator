package com.pritechvior.mealeastimatonew.data.model

data class Ingredient(
    val id: String,
    val name: String,
    val preparationTime: Int, // in minutes
    val predictedQuantity: Double? = null,
    val predictedUnit: String? = null,
    val isOptional: Boolean = false
)