package com.pritechvior.mealeastimatonew.util

object PortionCalculator {
    // Standard portion sizes per person
    private val standardPortions = mapOf(
        // Grains and Starches (in grams)
        "Rice" to 75.0,
        "Pasta" to 85.0,
        "Ugali" to 100.0,
        "Potatoes" to 150.0,
        
        // Proteins (in grams)
        "Meat" to 150.0,
        "Chicken" to 150.0,
        "Fish" to 150.0,
        "Beans" to 100.0,
        
        // Vegetables (in grams)
        "Tomatoes" to 75.0,
        "Onions" to 30.0,
        "Carrots" to 50.0,
        "Spinach" to 100.0,
        
        // Spices and Seasonings (in grams)
        "Salt" to 3.0,
        "Pepper" to 1.0,
        "Spices" to 5.0,
        
        // Cooking Oils (in ml)
        "Oil" to 15.0
    )

    private val units = mapOf(
        "Rice" to "grams",
        "Pasta" to "grams",
        "Ugali" to "grams",
        "Potatoes" to "grams",
        "Meat" to "grams",
        "Chicken" to "grams",
        "Fish" to "grams",
        "Beans" to "grams",
        "Tomatoes" to "grams",
        "Onions" to "grams",
        "Carrots" to "grams",
        "Spinach" to "grams",
        "Salt" to "grams",
        "Pepper" to "grams",
        "Spices" to "grams",
        "Oil" to "ml"
    )

    // Age group multipliers
    private val ageMultipliers = mapOf(
        "Child" to 0.6,    // Children eat about 60% of adult portion
        "Teen" to 1.2,     // Teenagers eat about 120% of adult portion
        "Adult" to 1.0,    // Base portion
        "Elderly" to 0.8   // Elderly eat about 80% of adult portion
    )

    // Meal type multipliers
    private val mealTypeMultipliers = mapOf(
        "Breakfast" to 0.8,  // Breakfast portions are usually smaller
        "Lunch" to 1.0,      // Standard portion
        "Dinner" to 1.0,     // Standard portion
        "Snack" to 0.5       // Snack portions are smaller
    )

    fun calculatePortion(
        ingredient: String,
        numberOfPeople: Int,
        ageGroups: List<String>,
        mealType: String
    ): Pair<Double, String> {
        // Get base portion size
        val basePortion = standardPortions[ingredient] ?: return Pair(0.0, "unknown")
        val unit = units[ingredient] ?: "units"

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
        return standardPortions.containsKey(ingredient)
    }

    fun getSupportedIngredients(): Set<String> {
        return standardPortions.keys
    }
} 