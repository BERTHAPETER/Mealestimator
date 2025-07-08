package com.pritechvior.mealeastimatonew

import android.app.Application
import android.util.Log
import com.pritechvior.mealeastimatonew.data.repository.RepositoryProvider

private const val TAG = "MealEstimatorApplication"

class MealEstimatorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Initialize repositories
            RepositoryProvider.initialize(this)
            RepositoryProvider.getDatabaseRepository()
            RepositoryProvider.getMealRepository()
            Log.d(TAG, "Repositories initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing repositories", e)
        }
    }
} 