package com.pritechvior.mealeastimatonew.data.repository

import com.pritechvior.mealeastimatonew.data.model.*

class MockRepository {

    // Mock Users
    private val mockUsers = listOf(
        User("1", "john_doe", "john@example.com", "password123"),
        User("2", "jane_smith", "jane@example.com", "password456"),
        User("3", "chef_mike", "mike@example.com", "cooking123")
    )

    // Mock Meals
    val mockMeals = listOf(
        Meal(
            id = "1",
            name = "Rice meet",
            description = "Classic tanzanian rice and meet",
            emoji = "🍝",
            imageUrl = null,
            basePreparationTime = 30,
            difficulty = "Medium"
        ),
        Meal(
            id = "2",
            name = "ugali beens",
            description = "fresh ugali been",
            emoji = "🍛",
            imageUrl = null,
            basePreparationTime = 45,
            difficulty = "Hard"
        ),
        Meal(
            id = "3",
            name = "Beef",
            description = "Quick and healthy beef",
            emoji = "🥘",
            imageUrl = null,
            basePreparationTime = 25,
            difficulty = "Medium"
        ),
        Meal(
            id = "4",
            name = "potato",
            description = "fresh potato",
            emoji = "🥞",
            imageUrl = null,
            basePreparationTime = 20,
            difficulty = "Easy"
        ),
        Meal(
            id = "5",
            name = "Fish rise",
            description = "Fresh fishi",
            emoji = "🌮",
            imageUrl = null,
            basePreparationTime = 35,
            difficulty = "Medium"
        )
    )

    // Mock Ingredients
    val mockIngredients = listOf(
        Ingredient("1", "Onions", 5),
        Ingredient("3", "Tomatoes", 8),
        Ingredient("4", "Bell Peppers", 6),
        Ingredient("5", "Chicken Breast", 15),
        Ingredient("6", "Rice", 20),
        Ingredient("7", "floods", 12),
        Ingredient("8", "oil", 2),
        Ingredient("9", "Herbs & Spices", 3),
        Ingredient("10", "Vegetables", 10)
    )

    fun authenticateUser(username: String, password: String): User? {
        return mockUsers.find { it.username == username && it.password == password }
    }

    fun registerUser(username: String, email: String, password: String): User {
        val newUser = User((mockUsers.size + 1).toString(), username, email, password)
        return newUser
    }

    fun getMeals(): List<Meal> = mockMeals

    fun getIngredients(): List<Ingredient> = mockIngredients

    fun calculateCookingTime(
        baseMealTime: Int,
        mealType: String,
        peopleCount: Int,
        selectedIngredients: List<Ingredient>
    ): Int {
        var totalTime = baseMealTime

        // Adjust for meal type
        totalTime += when (mealType.lowercase()) {
            "breakfast" -> -5 // Breakfast is usually quicker
            "dinner" -> 10   // Dinner takes more time
            else -> 0
        }

        // Adjust for number of people (more people = slightly more prep time)
        totalTime += when {
            peopleCount <= 2 -> 0
            peopleCount <= 4 -> 5
            peopleCount <= 6 -> 10
            else -> 15
        }

        // Add ingredient preparation time
        val ingredientTime = selectedIngredients.sumOf { it.preparationTime }
        totalTime += ingredientTime

        return maxOf(totalTime, 10) // Minimum 10 minutes
    }
}