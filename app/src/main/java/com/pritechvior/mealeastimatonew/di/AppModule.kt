package com.pritechvior.mealeastimatonew.di

import com.pritechvior.mealeastimatonew.data.repository.DatabaseRepository
import com.pritechvior.mealeastimatonew.data.repository.MealRepository
import com.pritechvior.mealeastimatonew.data.repository.RepositoryProvider
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel

object AppModule {
    fun provideDatabaseRepository(): DatabaseRepository {
        return RepositoryProvider.getDatabaseRepository()
    }

    fun provideMealRepository(): MealRepository {
        return RepositoryProvider.getMealRepository()
    }

    fun provideMealEstimatorViewModel(): MealEstimatorViewModel {
        return MealEstimatorViewModel(provideDatabaseRepository())
    }
} 