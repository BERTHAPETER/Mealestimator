package com.pritechvior.mealeastimatonew.data.model

data class FoodQuantities(
    val metadata: Metadata,
    val foods: List<FoodItem>
)

data class Metadata(
    val title: String,
    val description: String,
    val size_codes: Map<String, String>,
    val measurement_units: Map<String, String>
)

data class FoodItem(
    val code: Int,
    val name_kiswahili: String,
    val name_english: String,
    val portions: List<Portion>
)

data class Portion(
    val size: String,
    val plate: Int? = null,
    val saucer: Int? = null,
    val unit: Int? = null,
    val glass: Int? = null,
    val cup: Int? = null,
    val bowl: Int? = null,
    val average: Int? = null
)

object FoodQuantitiesData {
    // This will be populated from the JSON file
    private var foodQuantities: FoodQuantities? = null
    
    fun setFoodQuantities(data: FoodQuantities) {
        foodQuantities = data
    }
    
    fun getFoodQuantities(): FoodQuantities? = foodQuantities
    
    fun findFoodByName(englishName: String, swahiliName: String? = null): FoodItem? {
        return foodQuantities?.foods?.find { food ->
            food.name_english.equals(englishName, ignoreCase = true) ||
            (swahiliName != null && food.name_kiswahili.equals(swahiliName, ignoreCase = true))
        }
    }
    
    fun getAveragePortion(englishName: String, swahiliName: String? = null): Int? {
        val food = findFoodByName(englishName, swahiliName)
        return food?.portions?.find { it.size == "A" }?.average
    }
    
    fun getStandardPortion(englishName: String, swahiliName: String? = null): Int? {
        val food = findFoodByName(englishName, swahiliName)
        return food?.portions?.find { it.size == "Std" }?.let { portion ->
            portion.plate ?: portion.saucer ?: portion.unit ?: portion.glass ?: portion.cup ?: portion.bowl
        }
    }
    
    fun getMediumPortion(englishName: String, swahiliName: String? = null): Int? {
        val food = findFoodByName(englishName, swahiliName)
        return food?.portions?.find { it.size == "M" }?.unit
    }
    
    fun getPortionUnit(englishName: String, swahiliName: String? = null): String {
        val food = findFoodByName(englishName, swahiliName)
        return food?.portions?.firstOrNull()?.let { portion ->
            when {
                portion.plate != null -> "grams"
                portion.saucer != null -> "grams"
                portion.unit != null -> "grams"
                portion.glass != null -> "ml"
                portion.cup != null -> "ml"
                portion.bowl != null -> "grams"
                else -> "grams"
            }
        } ?: "grams"
    }
} 