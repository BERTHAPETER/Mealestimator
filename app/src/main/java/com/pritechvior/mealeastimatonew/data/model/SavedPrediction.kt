package com.pritechvior.mealeastimatonew.data.model

import java.util.UUID

data class SavedPrediction(
    val id: String = UUID.randomUUID().toString(),
    val mealName: String,
    val mealType: String,
    val numberOfPeople: Int,
    val ageGroups: List<String>,
    val ingredients: List<SavedIngredient>,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class SavedIngredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val isOptional: Boolean = false,
    val isCustomQuantity: Boolean = false
) 