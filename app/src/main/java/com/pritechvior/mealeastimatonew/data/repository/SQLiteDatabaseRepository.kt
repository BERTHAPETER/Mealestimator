package com.pritechvior.mealeastimatonew.data.repository

import android.content.Context
import com.pritechvior.mealeastimatonew.data.database.MealEstimatorDatabase
import com.pritechvior.mealeastimatonew.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.UUID

class SQLiteDatabaseRepository(context: Context) : DatabaseRepository {
    private val database = MealEstimatorDatabase(context)
    private var currentUser: User? = null
    private val appContext = context

    override fun getContext(): Context = appContext

    override suspend fun registerUser(
        username: String,
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = User(
                id = UUID.randomUUID().toString(),
                username = username,
                email = email,
                password = password
            )
            
            if (database.insertUser(user)) {
                currentUser = user
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to register user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(username: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val user = database.getUser(username, password)
                if (user != null) {
                    currentUser = user
                    Result.success(user)
                } else {
                    Result.failure(Exception("Invalid credentials"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getCurrentUser(): User? = currentUser

    override suspend fun logoutUser() {
        currentUser = null
    }

    override suspend fun updateUserPassword(userId: String, newPassword: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                if (database.updateUserPassword(userId, newPassword)) {
                    val updatedUser = currentUser?.copy(
                        password = newPassword,
                        updatedAt = System.currentTimeMillis()
                    )
                    currentUser = updatedUser
                    Result.success(updatedUser!!)
                } else {
                    Result.failure(Exception("Failed to update password"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateUserProfileImage(imageUri: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val user = currentUser ?: return@withContext Result.failure(Exception("No user logged in"))
                if (database.updateUserProfileImage(user.id, imageUri)) {
                    val updatedUser = user.copy(
                        profileImage = imageUri,
                        updatedAt = System.currentTimeMillis()
                    )
                    currentUser = updatedUser
                    Result.success(imageUri)
                } else {
                    Result.failure(Exception("Failed to update profile image"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteUserProfileImage(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val user = currentUser ?: return@withContext Result.failure(Exception("No user logged in"))
                if (database.deleteUserProfileImage(user.id)) {
                    val updatedUser = user.copy(
                        profileImage = null,
                        updatedAt = System.currentTimeMillis()
                    )
                    currentUser = updatedUser
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to delete profile image"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getAllMeals(): Flow<List<Meal>> = flow {
        emit(database.getAllMeals())
    }.flowOn(Dispatchers.IO)

    override suspend fun addMeal(meal: Meal) {
        withContext(Dispatchers.IO) {
            database.insertMeal(meal)
        }
    }

    override suspend fun getMealById(id: String): Meal? = withContext(Dispatchers.IO) {
        database.getAllMeals().find { it.id == id }
    }

    override suspend fun getAllIngredients(): Flow<List<Ingredient>> = flow {
        emit(database.getAllIngredients())
    }.flowOn(Dispatchers.IO)

    override suspend fun addIngredient(ingredient: Ingredient) {
        withContext(Dispatchers.IO) {
            database.insertIngredient(ingredient)
        }
    }

    override suspend fun getIngredientById(id: String): Ingredient? = withContext(Dispatchers.IO) {
        database.getAllIngredients().find { it.id == id }
    }

    override suspend fun getSavedPredictions(userId: String): Flow<List<SavedPrediction>> = flow {
        emit(database.getSavedPredictions(userId))
    }.flowOn(Dispatchers.IO)

    override suspend fun savePrediction(prediction: SavedPrediction, userId: String) {
        withContext(Dispatchers.IO) {
            database.insertSavedPrediction(prediction, userId)
        }
    }

    override suspend fun deletePrediction(predictionId: String) {
        // Implement deletion logic if needed
    }

    override suspend fun getPredictionById(predictionId: String): SavedPrediction? =
        withContext(Dispatchers.IO) {
            currentUser?.let { user ->
                database.getSavedPredictions(user.id).find { it.id == predictionId }
            }
        }

    override suspend fun deleteSavedPrediction(predictionId: String, userId: String) {
        database.deleteSavedPrediction(predictionId, userId)
    }
} 