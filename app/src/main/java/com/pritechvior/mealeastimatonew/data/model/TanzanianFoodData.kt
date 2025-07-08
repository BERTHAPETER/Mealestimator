package com.pritechvior.mealeastimatonew.data.model

data class FoodConsumptionData(
    val foodCategory: String,
    val adultIntakeGramsPerDay: Double,
    val childIntakeGramsPerDay: Double,
    val adultEnergyKcalPerDay: Double,
    val childEnergyKcalPerDay: Double,
    val adultProteinGramsPerDay: Double,
    val childProteinGramsPerDay: Double,
    val adultFatGramsPerDay: Double,
    val childFatGramsPerDay: Double
)

object TanzanianFoodData {
    val foodConsumptionData = listOf(
        // Basic ingredients
        FoodConsumptionData(
            foodCategory = "Rice",
            adultIntakeGramsPerDay = 150.0,
            childIntakeGramsPerDay = 100.0,
            adultEnergyKcalPerDay = 540.0,
            childEnergyKcalPerDay = 360.0,
            adultProteinGramsPerDay = 11.0,
            childProteinGramsPerDay = 7.3,
            adultFatGramsPerDay = 1.5,
            childFatGramsPerDay = 1.0
        ),
        FoodConsumptionData(
            foodCategory = "Beef",
            adultIntakeGramsPerDay = 120.0,
            childIntakeGramsPerDay = 80.0,
            adultEnergyKcalPerDay = 308.0,
            childEnergyKcalPerDay = 205.0,
            adultProteinGramsPerDay = 25.0,
            childProteinGramsPerDay = 16.7,
            adultFatGramsPerDay = 21.0,
            childFatGramsPerDay = 14.0
        ),
        // Additional Tanzanian staples
        FoodConsumptionData(
            foodCategory = "Cassava",
            adultIntakeGramsPerDay = 518.0,
            childIntakeGramsPerDay = 260.0,
            adultEnergyKcalPerDay = 829.0,
            childEnergyKcalPerDay = 390.0,
            adultProteinGramsPerDay = 7.3,
            childProteinGramsPerDay = 3.6,
            adultFatGramsPerDay = 1.6,
            childFatGramsPerDay = 0.8
        ),
        FoodConsumptionData(
            foodCategory = "Beans",
            adultIntakeGramsPerDay = 42.0,
            childIntakeGramsPerDay = 20.0,
            adultEnergyKcalPerDay = 143.0,
            childEnergyKcalPerDay = 68.0,
            adultProteinGramsPerDay = 8.4,
            childProteinGramsPerDay = 4.0,
            adultFatGramsPerDay = 0.8,
            childFatGramsPerDay = 0.4
        ),
        // Condiments and aromatics
        FoodConsumptionData(
            foodCategory = "Coconut Milk",
            adultIntakeGramsPerDay = 100.0,
            childIntakeGramsPerDay = 60.0,
            adultEnergyKcalPerDay = 230.0,
            childEnergyKcalPerDay = 138.0,
            adultProteinGramsPerDay = 2.3,
            childProteinGramsPerDay = 1.4,
            adultFatGramsPerDay = 23.8,
            childFatGramsPerDay = 14.3
        ),
        FoodConsumptionData(
            foodCategory = "Onions",
            adultIntakeGramsPerDay = 50.0,
            childIntakeGramsPerDay = 30.0,
            adultEnergyKcalPerDay = 20.0,
            childEnergyKcalPerDay = 12.0,
            adultProteinGramsPerDay = 0.9,
            childProteinGramsPerDay = 0.5,
            adultFatGramsPerDay = 0.1,
            childFatGramsPerDay = 0.1
        ),
        FoodConsumptionData(
            foodCategory = "Garlic",
            adultIntakeGramsPerDay = 10.0,
            childIntakeGramsPerDay = 5.0,
            adultEnergyKcalPerDay = 15.0,
            childEnergyKcalPerDay = 7.5,
            adultProteinGramsPerDay = 0.6,
            childProteinGramsPerDay = 0.3,
            adultFatGramsPerDay = 0.1,
            childFatGramsPerDay = 0.05
        ),
        FoodConsumptionData(
            foodCategory = "Ginger",
            adultIntakeGramsPerDay = 15.0,
            childIntakeGramsPerDay = 7.5,
            adultEnergyKcalPerDay = 18.0,
            childEnergyKcalPerDay = 9.0,
            adultProteinGramsPerDay = 0.4,
            childProteinGramsPerDay = 0.2,
            adultFatGramsPerDay = 0.2,
            childFatGramsPerDay = 0.1
        ),
        FoodConsumptionData(
            foodCategory = "Tomatoes",
            adultIntakeGramsPerDay = 100.0,
            childIntakeGramsPerDay = 60.0,
            adultEnergyKcalPerDay = 18.0,
            childEnergyKcalPerDay = 11.0,
            adultProteinGramsPerDay = 0.9,
            childProteinGramsPerDay = 0.5,
            adultFatGramsPerDay = 0.2,
            childFatGramsPerDay = 0.1
        ),
        // Spices and seasonings
        FoodConsumptionData(
            foodCategory = "Pilau Masala",
            adultIntakeGramsPerDay = 10.0,
            childIntakeGramsPerDay = 5.0,
            adultEnergyKcalPerDay = 25.0,
            childEnergyKcalPerDay = 12.5,
            adultProteinGramsPerDay = 1.0,
            childProteinGramsPerDay = 0.5,
            adultFatGramsPerDay = 1.2,
            childFatGramsPerDay = 0.6
        ),
        FoodConsumptionData(
            foodCategory = "Salt",
            adultIntakeGramsPerDay = 6.0,
            childIntakeGramsPerDay = 3.0,
            adultEnergyKcalPerDay = 0.0,
            childEnergyKcalPerDay = 0.0,
            adultProteinGramsPerDay = 0.0,
            childProteinGramsPerDay = 0.0,
            adultFatGramsPerDay = 0.0,
            childFatGramsPerDay = 0.0
        ),
        FoodConsumptionData(
            foodCategory = "Black Pepper",
            adultIntakeGramsPerDay = 3.0,
            childIntakeGramsPerDay = 1.5,
            adultEnergyKcalPerDay = 8.0,
            childEnergyKcalPerDay = 4.0,
            adultProteinGramsPerDay = 0.3,
            childProteinGramsPerDay = 0.15,
            adultFatGramsPerDay = 0.1,
            childFatGramsPerDay = 0.05
        ),
        // Additional categories from FoodConsumption.kt
        FoodConsumptionData(
            foodCategory = "Milk & eggs",
            adultIntakeGramsPerDay = 72.0,
            childIntakeGramsPerDay = 100.0,
            adultEnergyKcalPerDay = 50.0,
            childEnergyKcalPerDay = 66.0,
            adultProteinGramsPerDay = 3.8,
            childProteinGramsPerDay = 3.4,
            adultFatGramsPerDay = 2.9,
            childFatGramsPerDay = 3.6
        ),
        FoodConsumptionData(
            foodCategory = "Fish & seafood",
            adultIntakeGramsPerDay = 19.0,
            childIntakeGramsPerDay = 10.0,
            adultEnergyKcalPerDay = 39.0,
            childEnergyKcalPerDay = 21.0,
            adultProteinGramsPerDay = 3.6,
            childProteinGramsPerDay = 1.9,
            adultFatGramsPerDay = 2.3,
            childFatGramsPerDay = 1.1
        ),
        FoodConsumptionData(
            foodCategory = "Vegetable oils",
            adultIntakeGramsPerDay = 14.0,
            childIntakeGramsPerDay = 10.0,
            adultEnergyKcalPerDay = 126.0,
            childEnergyKcalPerDay = 90.0,
            adultProteinGramsPerDay = 0.0,
            childProteinGramsPerDay = 0.0,
            adultFatGramsPerDay = 14.0,
            childFatGramsPerDay = 10.0
        )
    )
} 