package com.pritechvior.mealeastimatonew.data.repository

import com.pritechvior.mealeastimatonew.data.model.Ingredient
import com.pritechvior.mealeastimatonew.data.model.Meal
import kotlinx.coroutines.flow.StateFlow

interface MealRepository {
    val meals: StateFlow<List<Meal>>
    val ingredients: StateFlow<List<Ingredient>>

    fun addMeal(meal: Meal)
    fun addIngredient(ingredient: Ingredient)
    fun getMealById(id: String): Meal?
    fun getIngredientById(id: String): Ingredient?
    fun updateMeal(meal: Meal)
    fun updateIngredient(ingredient: Ingredient)
    fun deleteMeal(id: String)
    fun deleteIngredient(id: String)
    fun clearAll()
} 