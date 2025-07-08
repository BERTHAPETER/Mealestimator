package com.pritechvior.mealeastimatonew.data.repository

import android.util.Log
import com.pritechvior.mealeastimatonew.data.model.Ingredient
import com.pritechvior.mealeastimatonew.data.model.Meal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "MealRepositoryImpl"

class MealRepositoryImpl : MealRepository {
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())

    override val meals: StateFlow<List<Meal>> = _meals.asStateFlow()
    override val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    override fun addMeal(meal: Meal) {
        try {
            val currentMeals = _meals.value
            if (!currentMeals.any { it.id == meal.id }) {
                _meals.value = currentMeals + meal
                Log.d(TAG, "Meal added successfully: ${meal.name}")
            } else {
                Log.w(TAG, "Meal with ID ${meal.id} already exists")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding meal", e)
        }
    }

    override fun addIngredient(ingredient: Ingredient) {
        try {
            val currentIngredients = _ingredients.value
            if (!currentIngredients.any { it.id == ingredient.id }) {
                _ingredients.value = currentIngredients + ingredient
                Log.d(TAG, "Ingredient added successfully: ${ingredient.name}")
            } else {
                Log.w(TAG, "Ingredient with ID ${ingredient.id} already exists")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding ingredient", e)
        }
    }

    override fun getMealById(id: String): Meal? {
        return try {
            _meals.value.find { it.id == id }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting meal by ID: $id", e)
            null
        }
    }

    override fun getIngredientById(id: String): Ingredient? {
        return try {
            _ingredients.value.find { it.id == id }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ingredient by ID: $id", e)
            null
        }
    }

    override fun updateMeal(meal: Meal) {
        try {
            val currentMeals = _meals.value
            _meals.value = currentMeals.map { 
                if (it.id == meal.id) meal else it 
            }
            Log.d(TAG, "Meal updated successfully: ${meal.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating meal", e)
        }
    }

    override fun updateIngredient(ingredient: Ingredient) {
        try {
            val currentIngredients = _ingredients.value
            _ingredients.value = currentIngredients.map { 
                if (it.id == ingredient.id) ingredient else it 
            }
            Log.d(TAG, "Ingredient updated successfully: ${ingredient.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating ingredient", e)
        }
    }

    override fun deleteMeal(id: String) {
        try {
            val currentMeals = _meals.value
            _meals.value = currentMeals.filter { it.id != id }
            Log.d(TAG, "Meal deleted successfully: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting meal", e)
        }
    }

    override fun deleteIngredient(id: String) {
        try {
            val currentIngredients = _ingredients.value
            _ingredients.value = currentIngredients.filter { it.id != id }
            Log.d(TAG, "Ingredient deleted successfully: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting ingredient", e)
        }
    }

    override fun clearAll() {
        try {
            _meals.value = emptyList()
            _ingredients.value = emptyList()
            Log.d(TAG, "All meals and ingredients cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing meals and ingredients", e)
        }
    }
} 