package com.pritechvior.mealeastimatonew.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pritechvior.mealeastimatonew.data.repository.SQLiteDatabaseRepository

class MealEstimatorViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealEstimatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealEstimatorViewModel(
                databaseRepository = SQLiteDatabaseRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 