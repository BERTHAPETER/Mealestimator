package com.pritechvior.mealeastimatonew.data.repository

object RepositoryProvider {
    private val mealRepository = MealRepository().also {
        TanzanianMealsInitializer.initializeMealsAndIngredients(it)
    }

    fun getMealRepository(): MealRepository = mealRepository
} 
 