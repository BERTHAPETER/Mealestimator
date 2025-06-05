package com.pritechvior.mealeastimatonew.data.repository

import com.pritechvior.mealeastimatonew.data.model.Ingredient
import com.pritechvior.mealeastimatonew.data.model.Meal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MealRepository {
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    fun addMeal(meal: Meal) {
        _meals.value = _meals.value + meal
    }

    fun addIngredient(ingredient: Ingredient) {
        _ingredients.value = _ingredients.value + ingredient
    }

    fun removeMeal(mealId: String) {
        _meals.value = _meals.value.filter { it.id != mealId }
    }

    fun removeIngredient(ingredientId: String) {
        _ingredients.value = _ingredients.value.filter { it.id != ingredientId }
    }

    fun updateMeal(meal: Meal) {
        _meals.value = _meals.value.map { if (it.id == meal.id) meal else it }
    }

    fun updateIngredient(ingredient: Ingredient) {
        _ingredients.value = _ingredients.value.map { if (it.id == ingredient.id) ingredient else it }
    }

    fun clearAll() {
        _meals.value = emptyList()
        _ingredients.value = emptyList()
    }
} 