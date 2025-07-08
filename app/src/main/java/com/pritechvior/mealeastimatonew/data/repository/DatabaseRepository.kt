package com.pritechvior.mealeastimatonew.data.repository

import android.content.Context
import com.pritechvior.mealeastimatonew.data.model.*
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getContext(): Context
    
    // User operations
    suspend fun registerUser(username: String, email: String, password: String): Result<User>
    suspend fun loginUser(username: String, password: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun logoutUser()
    suspend fun updateUserPassword(userId: String, newPassword: String): Result<User>
    suspend fun updateUserProfileImage(imageUri: String): Result<String>
    suspend fun deleteUserProfileImage(): Result<Unit>

    // Meal operations
    suspend fun getAllMeals(): Flow<List<Meal>>
    suspend fun addMeal(meal: Meal)
    suspend fun getMealById(id: String): Meal?

    // Ingredient operations
    suspend fun getAllIngredients(): Flow<List<Ingredient>>
    suspend fun addIngredient(ingredient: Ingredient)
    suspend fun getIngredientById(id: String): Ingredient?

    // Saved Prediction operations
    suspend fun getSavedPredictions(userId: String): Flow<List<SavedPrediction>>
    suspend fun savePrediction(prediction: SavedPrediction, userId: String)
    suspend fun deletePrediction(predictionId: String)
    suspend fun getPredictionById(predictionId: String): SavedPrediction?
    suspend fun deleteSavedPrediction(predictionId: String, userId: String)
} 