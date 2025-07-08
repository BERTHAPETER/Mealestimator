package com.pritechvior.mealeastimatonew.data.repository

import android.content.Context
import android.util.Log
import com.pritechvior.mealeastimatonew.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

private const val TAG = "DatabaseRepositoryImpl"

class DatabaseRepositoryImpl(private val context: Context) : DatabaseRepository {
    private val users = mutableMapOf<String, User>()
    private val meals = MutableStateFlow<List<Meal>>(emptyList())
    private val ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    private val savedPredictions = mutableMapOf<String, MutableStateFlow<List<SavedPrediction>>>()
    private var currentUser: User? = null

    override fun getContext(): Context = context

    override suspend fun registerUser(username: String, email: String, password: String): Result<User> {
        return try {
            if (users.values.any { it.username == username }) {
                Result.failure(Exception("Username already exists"))
            } else if (users.values.any { it.email == email }) {
                Result.failure(Exception("Email already exists"))
            } else {
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    username = username,
                    email = email,
                    password = password
                )
                users[newUser.id] = newUser
                currentUser = newUser
                Log.d(TAG, "User registered successfully: ${newUser.username}")
                Result.success(newUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering user", e)
            Result.failure(e)
        }
    }

    override suspend fun loginUser(username: String, password: String): Result<User> {
        return try {
            val user = users.values.find { it.username == username && it.password == password }
            if (user != null) {
                currentUser = user
                Log.d(TAG, "User logged in successfully: ${user.username}")
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error logging in user", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? = currentUser

    override suspend fun logoutUser() {
        currentUser = null
        Log.d(TAG, "User logged out")
    }

    override suspend fun updateUserPassword(userId: String, newPassword: String): Result<User> {
        return try {
            val user = users[userId] ?: return Result.failure(Exception("User not found"))
            val updatedUser = user.copy(password = newPassword)
            users[userId] = updatedUser
            if (currentUser?.id == userId) {
                currentUser = updatedUser
            }
            Log.d(TAG, "Password updated successfully for user: ${user.username}")
            Result.success(updatedUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating password", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfileImage(imageUri: String): Result<String> {
        return try {
            val user = currentUser ?: return Result.failure(Exception("No user logged in"))
            val updatedUser = user.copy(profileImage = imageUri)
            users[user.id] = updatedUser
            currentUser = updatedUser
            Log.d(TAG, "Profile image updated successfully for user: ${user.username}")
            Result.success(imageUri)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile image", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteUserProfileImage(): Result<Unit> {
        return try {
            val user = currentUser ?: return Result.failure(Exception("No user logged in"))
            val updatedUser = user.copy(profileImage = null)
            users[user.id] = updatedUser
            currentUser = updatedUser
            Log.d(TAG, "Profile image deleted successfully for user: ${user.username}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting profile image", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllMeals(): Flow<List<Meal>> = meals

    override suspend fun addMeal(meal: Meal) {
        meals.value = meals.value + meal
        Log.d(TAG, "Meal added successfully: ${meal.name}")
    }

    override suspend fun getMealById(id: String): Meal? {
        return meals.value.find { it.id == id }
    }

    override suspend fun getAllIngredients(): Flow<List<Ingredient>> = ingredients

    override suspend fun addIngredient(ingredient: Ingredient) {
        ingredients.value = ingredients.value + ingredient
        Log.d(TAG, "Ingredient added successfully: ${ingredient.name}")
    }

    override suspend fun getIngredientById(id: String): Ingredient? {
        return ingredients.value.find { it.id == id }
    }

    override suspend fun getSavedPredictions(userId: String): Flow<List<SavedPrediction>> {
        return savedPredictions.getOrPut(userId) { MutableStateFlow(emptyList()) }
    }

    override suspend fun savePrediction(prediction: SavedPrediction, userId: String) {
        val userPredictions = savedPredictions.getOrPut(userId) { MutableStateFlow(emptyList()) }
        userPredictions.value = userPredictions.value + prediction
        Log.d(TAG, "Prediction saved successfully for user: $userId")
    }

    override suspend fun deletePrediction(predictionId: String) {
        savedPredictions.values.forEach { predictions ->
            predictions.value = predictions.value.filter { it.id != predictionId }
        }
        Log.d(TAG, "Prediction deleted successfully: $predictionId")
    }

    override suspend fun getPredictionById(predictionId: String): SavedPrediction? {
        return savedPredictions.values.firstOrNull { predictions ->
            predictions.value.any { it.id == predictionId }
        }?.value?.find { it.id == predictionId }
    }

    override suspend fun deleteSavedPrediction(predictionId: String, userId: String) {
        // Assuming you have a map or database logic for user predictions
        val userPredictions = savedPredictions[userId]
        userPredictions?.let {
            val updated = it.value.filter { pred -> pred.id != predictionId }
            it.value = updated
        }
        // If using a real database, call the appropriate delete method here
    }
} 