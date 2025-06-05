package com.pritechvior.mealeastimatonew.data.repository

import com.pritechvior.mealeastimatonew.data.model.Ingredient
import com.pritechvior.mealeastimatonew.data.model.Meal
import java.util.UUID

object TanzanianMealsInitializer {
    fun initializeMealsAndIngredients(repository: MealRepository) {
        // Clear existing data
        repository.clearAll()

        // Add ingredients
        val ingredients = listOf(
            // Rice and grains
            createIngredient("Rice", 10),
            createIngredient("Coconut Milk", 2),
            createIngredient("Cardamom", 1),
            createIngredient("Cloves", 1),
            createIngredient("Cinnamon", 1),
            
            // Proteins
            createIngredient("Beef", 15),
            createIngredient("Chicken", 15),
            createIngredient("Fish", 10),
            createIngredient("Octopus", 20),
            
            // Vegetables and fruits
            createIngredient("Spinach", 5),
            createIngredient("Cassava Leaves", 10),
            createIngredient("Green Bananas", 15),
            createIngredient("Plantains", 15),
            createIngredient("Coconut", 10),
            createIngredient("Tomatoes", 5),
            createIngredient("Onions", 5),
            createIngredient("Garlic", 2),
            createIngredient("Ginger", 2),
            
            // Legumes and starches
            createIngredient("Beans", 60),
            createIngredient("Cassava", 30),
            createIngredient("Sweet Potatoes", 25),
            createIngredient("Corn Flour", 5),
            
            // Spices and seasonings
            createIngredient("Curry Powder", 1),
            createIngredient("Turmeric", 1),
            createIngredient("Black Pepper", 1),
            createIngredient("Salt", 1),
            createIngredient("Tamarind", 5),
            createIngredient("Lime", 2),
            
            // Additional ingredients
            createIngredient("Peanuts", 5),
            createIngredient("Coconut Cream", 2),
            createIngredient("Lemongrass", 2)
        )
        
        ingredients.forEach { repository.addIngredient(it) }

        // Add meals
        val meals = listOf(
            // Rice dishes
            createMeal(
                "Pilau",
                "Spiced rice cooked with meat and aromatic spices",
                30,
                "Medium",
                "🍚"
            ),
            createMeal(
                "Wali wa Nazi",
                "Coconut rice, a coastal favorite",
                25,
                "Easy",
                "🥥"
            ),
            
            // Meat dishes
            createMeal(
                "Nyama Choma",
                "Grilled meat (usually beef or goat) served with accompaniments",
                45,
                "Medium",
                "🍖"
            ),
            createMeal(
                "Mchuzi wa Kuku",
                "Tanzanian chicken curry",
                40,
                "Medium",
                "🍗"
            ),
            
            // Seafood dishes
            createMeal(
                "Pweza wa Nazi",
                "Octopus in coconut curry",
                50,
                "Hard",
                "🐙"
            ),
            createMeal(
                "Samaki wa Kupaka",
                "Grilled fish in coconut sauce",
                35,
                "Medium",
                "🐟"
            ),
            
            // Stews and curries
            createMeal(
                "Maharage ya Nazi",
                "Beans in coconut sauce",
                75,
                "Easy",
                "🫘"
            ),
            createMeal(
                "Ndizi na Nyama",
                "Plantains with meat stew",
                45,
                "Medium",
                "🍌"
            ),
            
            // Vegetable dishes
            createMeal(
                "Kisamvu",
                "Cassava leaves with coconut",
                40,
                "Medium",
                "🌿"
            ),
            createMeal(
                "Mchicha",
                "Tanzanian spinach",
                20,
                "Easy",
                "🥬"
            ),
            
            // Street food / snacks
            createMeal(
                "Mishkaki",
                "Tanzanian beef skewers",
                25,
                "Easy",
                "🥩"
            ),
            createMeal(
                "Muhogo wa Nazi",
                "Cassava in coconut sauce",
                35,
                "Easy",
                "🥔"
            ),
            
            // Additional traditional dishes
            createMeal(
                "Ugali",
                "Stiff cornmeal porridge",
                20,
                "Easy",
                "🥣"
            ),
            createMeal(
                "Makande",
                "Corn and beans mixture",
                90,
                "Medium",
                "🌽"
            ),
            createMeal(
                "Vitumbua",
                "Coconut rice pancakes",
                40,
                "Medium",
                "🥞"
            )
        )
        
        meals.forEach { repository.addMeal(it) }
    }

    private fun createIngredient(name: String, prepTime: Int): Ingredient {
        return Ingredient(
            id = UUID.randomUUID().toString(),
            name = name,
            preparationTime = prepTime
        )
    }

    private fun createMeal(
        name: String,
        description: String,
        prepTime: Int,
        difficulty: String,
        emoji: String
    ): Meal {
        return Meal(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            basePreparationTime = prepTime,
            difficulty = difficulty,
            emoji = emoji
        )
    }
} 