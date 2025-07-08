package com.pritechvior.mealeastimatonew.data.repository

import android.content.Context

object RepositoryProvider {
    private var databaseRepository: DatabaseRepository? = null
    private var mealRepository: MealRepository? = null
    private var context: Context? = null

    fun initialize(appContext: Context) {
        context = appContext.applicationContext
    }

    fun getDatabaseRepository(): DatabaseRepository {
        val ctx = context ?: throw IllegalStateException("RepositoryProvider not initialized")
        return databaseRepository ?: SQLiteDatabaseRepository(ctx).also {
            databaseRepository = it
        }
    }

    fun getMealRepository(): MealRepository {
        return mealRepository ?: MealRepositoryImpl().also {
            mealRepository = it
        }
    }
} 
 