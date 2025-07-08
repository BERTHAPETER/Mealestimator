package com.pritechvior.mealeastimatonew.util

import android.content.Context
import com.google.gson.Gson
import com.pritechvior.mealeastimatonew.data.model.FoodQuantities
import com.pritechvior.mealeastimatonew.data.model.FoodQuantitiesData

object JsonLoader {
    fun loadFoodQuantities(context: Context): FoodQuantities? {
        return try {
            val jsonString = context.assets.open("food_quantities.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val foodQuantities = gson.fromJson(jsonString, FoodQuantities::class.java)
            
            // Set the data in FoodQuantitiesData
            FoodQuantitiesData.setFoodQuantities(foodQuantities)
            
            foodQuantities
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
} 