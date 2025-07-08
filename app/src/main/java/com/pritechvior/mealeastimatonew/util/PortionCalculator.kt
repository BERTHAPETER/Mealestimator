package com.pritechvior.mealeastimatonew.util

import com.pritechvior.mealeastimatonew.data.model.FoodQuantitiesData
import com.pritechvior.mealeastimatonew.data.model.TanzanianFoodData
import com.pritechvior.mealeastimatonew.data.model.Person

object PortionCalculator {
    // Constants
    private const val TEEN_FACTOR = 1.2  // Teenagers eat about 120% of adult portion

    // Age group multipliers (adjusted for Tanzanian portions)
    private val ageMultipliers = mapOf(
        "Child" to 0.5,    // Children eat about 50% of adult portion
        "Teen" to 1.2,     // Teenagers eat about 120% of adult portion
        "Adult" to 1.0,    // Base portion
        "Elderly" to 0.8   // Elderly eat about 80% of adult portion
    )

    // Meal type multipliers (adjusted for Tanzanian meals)
    private val mealTypeMultipliers = mapOf(
        "Breakfast" to 0.7,  // Breakfast portions are usually smaller
        "Lunch" to 1.0,      // Standard portion
        "Dinner" to 1.2,     // Dinner portions are often larger in Tanzania
        "Snack" to 0.4       // Snack portions are smaller
    )

    private val genderMultipliers = mapOf(
        "Male" to 1.1,
        "Female" to 1.0
    )

    data class PortionResult(
        val totalAmount: Double,
        val localUnit: String,
        val localUnitAmount: Double?,
        val standardUnit: String,
        val standardAmount: Double
    )

    fun calculateIngredientPortionDetailed(
        ingredient: String,
        people: List<Person>, // Person should have ageGroup and gender
        mealType: String = "Lunch"
    ): PortionResult {
        val averagePortion = FoodQuantitiesData.getAveragePortion(ingredient)
        val standardPortion = FoodQuantitiesData.getStandardPortion(ingredient)
        val mediumPortion = FoodQuantitiesData.getMediumPortion(ingredient)
        val foodItem = FoodQuantitiesData.findFoodByName(ingredient)
        val basePortionPerPerson = when {
            averagePortion != null -> averagePortion.toDouble()
            standardPortion != null -> standardPortion.toDouble()
            mediumPortion != null -> mediumPortion.toDouble()
            else -> getFallbackPortion(ingredient)
        }
        val usedFallback = averagePortion == null && standardPortion == null && mediumPortion == null
        val unit = FoodQuantitiesData.getPortionUnit(ingredient) ?: getFallbackUnit(ingredient)

        // Group correction: if group is large, apply sharing factor
        val groupCorrection = if (people.size >= 5) 0.75 else 1.0

        // Age/gender multipliers
        var totalQuantity = 0.0
        for (person in people) {
            val ageMultiplier = ageMultipliers[person.ageGroup] ?: 1.0
            val genderMultiplier = genderMultipliers[person.gender] ?: 1.0
            totalQuantity += basePortionPerPerson * ageMultiplier * genderMultiplier
        }
        totalQuantity *= groupCorrection

        // Find the local unit and its value from the JSON
        var localUnit: String = unit
        var localUnitValue: Double? = null
        if (foodItem != null) {
            val portion = foodItem.portions.find { it.size == "Std" } ?: foodItem.portions.firstOrNull()
            when {
                portion?.plate != null -> { localUnit = "plate"; localUnitValue = portion.plate.toDouble() }
                portion?.saucer != null -> { localUnit = "saucer"; localUnitValue = portion.saucer.toDouble() }
                portion?.cup != null -> { localUnit = "cup"; localUnitValue = portion.cup.toDouble() }
                portion?.bowl != null -> { localUnit = "bowl"; localUnitValue = portion.bowl.toDouble() }
                portion?.glass != null -> { localUnit = "glass"; localUnitValue = portion.glass.toDouble() }
                portion?.unit != null -> { localUnit = "unit"; localUnitValue = portion.unit.toDouble() }
                portion?.average != null -> { localUnit = unit; localUnitValue = portion.average.toDouble() }
            }
        }

        // Calculate how many local units this is
        val localUnitAmount = if (localUnitValue != null && localUnitValue > 0) totalQuantity / localUnitValue else null

        // Standardize to grams or ml for display
        val (standardUnit, standardAmount) = when {
            unit == "ml" && totalQuantity >= 1000 -> Pair("liters", totalQuantity / 1000.0)
            unit == "grams" && totalQuantity >= 1000 -> Pair("kg", totalQuantity / 1000.0)
            else -> Pair(unit, totalQuantity)
        }

        return PortionResult(
            totalAmount = totalQuantity,
            localUnit = localUnit,
            localUnitAmount = localUnitAmount,
            standardUnit = standardUnit,
            standardAmount = standardAmount
        )
    }

    fun calculateIngredientQuantity(
        ingredient: String,
        numberOfPeople: Int,
        ageGroups: List<String>,
        genders: List<String>? = null // Optional for backward compatibility
    ): Pair<Double, String> {
        val averagePortion = FoodQuantitiesData.getAveragePortion(ingredient)
        val standardPortion = FoodQuantitiesData.getStandardPortion(ingredient)
        val mediumPortion = FoodQuantitiesData.getMediumPortion(ingredient)
        val basePortionPerPerson = when {
            averagePortion != null -> averagePortion.toDouble()
            standardPortion != null -> standardPortion.toDouble()
            mediumPortion != null -> mediumPortion.toDouble()
            else -> getFallbackPortion(ingredient)
        }
        val unit = FoodQuantitiesData.getPortionUnit(ingredient) ?: getFallbackUnit(ingredient)

        var totalQuantity = 0.0
        for (i in ageGroups.indices) {
            val ageMultiplier = ageMultipliers[ageGroups[i]] ?: 1.0
            val genderMultiplier = genders?.getOrNull(i)?.let { genderMultipliers[it] } ?: 1.0
            val personQuantity = basePortionPerPerson * ageMultiplier * genderMultiplier
            totalQuantity += personQuantity
        }

        val roundedQuantity = when {
            totalQuantity < 1 -> Math.round(totalQuantity * 10.0) / 10.0
            totalQuantity < 10 -> Math.round(totalQuantity).toDouble()
            totalQuantity < 100 -> Math.round(totalQuantity / 5.0) * 5.0
            else -> Math.round(totalQuantity / 25.0) * 25.0
        }

        val finalPair = when {
            unit == "ml" && roundedQuantity >= 1000 -> Pair(roundedQuantity / 1000.0, "liters")
            unit == "grams" && roundedQuantity >= 1000 -> Pair(roundedQuantity / 1000.0, "kg")
            else -> Pair(roundedQuantity, unit)
        }
        return finalPair
    }

    private fun getFallbackPortion(ingredient: String): Double {
        // Fallback portions for ingredients not in the database
        return when (ingredient.lowercase()) {
            "maize flour", "unga wa mahindi" -> 100.0
            "water", "maji" -> 200.0
            "salt", "chumvi" -> 2.0
            "oil", "mafuta" -> 15.0
            "sugar", "sukari" -> 15.0
            "milk", "maziwa" -> 100.0
            "baking powder" -> 2.0
            "cheese", "jibini" -> 20.0
            "egg", "yai" -> 50.0
            "eggs", "mayai" -> 50.0
            "flour", "unga" -> 80.0
            "wheat flour", "unga wa ngano" -> 80.0
            "meat", "nyama" -> 150.0
            "beef", "nyama ya ng'ombe" -> 150.0
            "chicken", "kuku" -> 120.0
            "fish", "samaki" -> 120.0
            "octopus", "pweza" -> 100.0
            "potatoes", "viazi" -> 80.0
            "rice", "mchele" -> 75.0
            "beans", "maharage" -> 60.0
            "red kidney beans", "maharage nyekundu" -> 60.0
            "onion", "kitunguu" -> 30.0
            "onions", "vitunguu" -> 30.0
            "garlic", "kitunguu saumu" -> 5.0
            "ginger", "tangawizi" -> 5.0
            "tomato", "nyanya" -> 50.0
            "tomatoes", "nyanya" -> 50.0
            "carrots", "karoti" -> 40.0
            "cabbage", "kabichi" -> 30.0
            "peas", "kunde" -> 30.0
            "green pepper", "pilipili hoho" -> 20.0
            "coconut milk", "nazi" -> 100.0
            "lemon", "limu" -> 10.0
            "lime", "ndimu" -> 10.0
            "curry powder", "bizari ya pilau" -> 3.0
            "curry spices", "viungo vya pilau" -> 3.0
            "chili", "pilipili" -> 3.0
            "turmeric", "manjano" -> 2.0
            "coriander", "korianda" -> 2.0
            "cumin", "binzari" -> 1.0
            "cinnamon", "mdalasini" -> 1.0
            "cardamom", "iliki" -> 1.0
            "cloves", "karafuu" -> 0.5
            "pepper", "pilipili" -> 1.0
            "black pepper", "pilipili nyeusi" -> 1.0
            else -> 50.0 // Default fallback
        }
    }

    private fun getFallbackUnit(ingredient: String): String {
        return when (ingredient.lowercase()) {
            "water", "maji", "milk", "maziwa", "coconut milk", "nazi", "oil", "mafuta" -> "ml"
            else -> "grams"
        }
    }

    fun calculateNutrition(
        ingredient: String,
        quantity: Double,
        unit: String,
        ageGroups: List<String>
    ): Triple<Double, Double, Double> {
        val foodData = TanzanianFoodData.foodConsumptionData.find { 
            it.foodCategory.equals(ingredient, ignoreCase = true)
        } ?: return Triple(0.0, 0.0, 0.0)

        // Convert quantity to grams if necessary
        val quantityInGrams = when (unit) {
            "kg" -> quantity * 1000
            "liters" -> quantity * 1000
            else -> quantity
        }

        // Calculate proportional nutrition values
        val proportionOfDaily = quantityInGrams / foodData.adultIntakeGramsPerDay

        val energy = foodData.adultEnergyKcalPerDay * proportionOfDaily
        val protein = foodData.adultProteinGramsPerDay * proportionOfDaily
        val fat = foodData.adultFatGramsPerDay * proportionOfDaily

        // Round to 1 decimal place
        return Triple(
            Math.round(energy * 10.0) / 10.0,
            Math.round(protein * 10.0) / 10.0,
            Math.round(fat * 10.0) / 10.0
        )
    }

    fun suggestServingSize(ingredient: String, ageGroup: String): Double {
        val foodData = TanzanianFoodData.foodConsumptionData.find { 
            it.foodCategory.contains(ingredient, ignoreCase = true)
        } ?: return 0.0

        return when (ageGroup) {
            "Adult" -> foodData.adultIntakeGramsPerDay
            "Teen" -> foodData.adultIntakeGramsPerDay * TEEN_FACTOR
            "Child" -> foodData.childIntakeGramsPerDay
            else -> 0.0
        }
    }

    fun calculatePortion(
        ingredient: String,
        numberOfPeople: Int,
        ageGroups: List<String>,
        mealType: String
    ): Pair<Double, String> {
        // Get base portion size from food quantities database
        val averagePortion = FoodQuantitiesData.getAveragePortion(ingredient)
        val basePortion = averagePortion?.toDouble() ?: getFallbackPortion(ingredient)
        val unit = FoodQuantitiesData.getPortionUnit(ingredient) ?: getFallbackUnit(ingredient)

        // Calculate age-adjusted portion
        var totalPortion = 0.0
        ageGroups.forEach { ageGroup ->
            val ageMultiplier = ageMultipliers[ageGroup] ?: 1.0
            totalPortion += basePortion * ageMultiplier
        }

        // Apply meal type multiplier
        val mealMultiplier = mealTypeMultipliers[mealType] ?: 1.0
        totalPortion *= mealMultiplier

        // Round to reasonable numbers
        val roundedPortion = when {
            totalPortion < 10 -> Math.round(totalPortion * 10.0) / 10.0  // Round to 1 decimal
            totalPortion < 100 -> Math.round(totalPortion / 5.0) * 5.0   // Round to nearest 5
            else -> Math.round(totalPortion / 25.0) * 25.0               // Round to nearest 25
        }

        return Pair(roundedPortion, unit)
    }

    fun isIngredientSupported(ingredient: String): Boolean {
        // Check if ingredient exists in food quantities database
        val hasFoodData = FoodQuantitiesData.findFoodByName(ingredient) != null
        // Also check if we have fallback data
        val hasFallbackData = getFallbackPortion(ingredient) > 0
        return hasFoodData || hasFallbackData
    }

    fun getSupportedIngredients(): Set<String> {
        // Return all ingredients that have either food quantities data or fallback data
        val foodQuantitiesIngredients = FoodQuantitiesData.getFoodQuantities()?.foods?.map { it.name_english }?.toSet() ?: emptySet()
        val fallbackIngredients = setOf(
            "Maize flour", "Water", "Salt", "Oil", "Sugar", "Milk", "Baking powder", "Cheese", 
            "Egg", "Eggs", "Flour", "Wheat flour", "Meat", "Beef", "Chicken", "Fish", "Octopus", 
            "Potatoes", "Rice", "Beans", "Red kidney beans", "Onion", "Onions", "Garlic", "Ginger", 
            "Tomato", "Tomatoes", "Carrots", "Cabbage", "Peas", "Green pepper", "Coconut milk", 
            "Lemon", "Lime", "Curry powder", "Curry spices", "Chili", "Turmeric", "Coriander", 
            "Cumin", "Cinnamon", "Cardamom", "Cloves", "Pepper", "Black pepper"
        )
        return foodQuantitiesIngredients + fallbackIngredients
    }
} 