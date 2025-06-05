package com.pritechvior.mealeastimatonew.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pritechvior.mealeastimatonew.data.model.*
import com.pritechvior.mealeastimatonew.data.repository.IngredientPredictionRepository
import com.pritechvior.mealeastimatonew.data.repository.RepositoryProvider
import com.pritechvior.mealeastimatonew.data.repository.SavedPredictionRepository
import com.pritechvior.mealeastimatonew.di.NetworkModule
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID

class MealEstimatorViewModel : ViewModel() {
    private val predictionRepository = IngredientPredictionRepository()
    private val mealRepository = RepositoryProvider.getMealRepository()
    private val savedPredictionRepository = SavedPredictionRepository()

    val meals: StateFlow<List<Meal>> = mealRepository.meals
    val ingredients: StateFlow<List<Ingredient>> = mealRepository.ingredients
    val savedPredictions: StateFlow<List<SavedPrediction>> = savedPredictionRepository.savedPredictions

    var currentUser by mutableStateOf<User?>(null)
        private set

    var selectedMeal by mutableStateOf<Meal?>(null)
        private set

    var selectedMealType by mutableStateOf("")
        private set

    var people by mutableStateOf<List<Person>>(emptyList())
        private set

    var selectedIngredients by mutableStateOf<List<Ingredient>>(emptyList())
        private set

    var isPredicting by mutableStateOf(false)
        private set

    var predictionError by mutableStateOf<String?>(null)
        private set

    // Add initial data
    init {
        // Add traditional Tanzanian meals
        val meals = listOf(
            Meal(
                id = UUID.randomUUID().toString(),
                name = "Pilau",
                description = "Traditional Tanzanian spiced rice dish",
                emoji = "🍚",
                imageUrl = null,
                basePreparationTime = 45,
                difficulty = "Medium"
            ),
            Meal(
                id = UUID.randomUUID().toString(),
                name = "Nyama Choma",
                description = "Grilled meat with spices",
                emoji = "🍖",
                imageUrl = null,
                basePreparationTime = 60,
                difficulty = "Medium"
            ),
            Meal(
                id = UUID.randomUUID().toString(),
                name = "Wali wa Nazi",
                description = "Coconut rice",
                emoji = "🥥",
                imageUrl = null,
                basePreparationTime = 30,
                difficulty = "Easy"
            )
        )

        // Add common Tanzanian ingredients with default quantities
        val ingredients = listOf(
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Rice",
                preparationTime = 20
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Beef",
                preparationTime = 45
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Coconut Milk",
                preparationTime = 5
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Onions",
                preparationTime = 10
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Garlic",
                preparationTime = 5
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Ginger",
                preparationTime = 5
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Tomatoes",
                preparationTime = 10
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Pilau Masala",
                preparationTime = 5
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Salt",
                preparationTime = 1
            ),
            Ingredient(
                id = UUID.randomUUID().toString(),
                name = "Black Pepper",
                preparationTime = 1
            )
        )

        // Add all meals and ingredients
        meals.forEach { addMeal(it) }
        ingredients.forEach { addIngredient(it) }

        // Add some sample saved predictions
        val samplePrediction = SavedPrediction(
            id = UUID.randomUUID().toString(),
            mealName = "Pilau",
            mealType = "Lunch",
            numberOfPeople = 4,
            ageGroups = listOf("Adult", "Adult", "Teen", "Child"),
            ingredients = listOf(
                SavedIngredient("Rice", 2.0, "cups", false),
                SavedIngredient("Beef", 500.0, "grams", false),
                SavedIngredient("Onions", 2.0, "pieces", false),
                SavedIngredient("Pilau Masala", 2.0, "tablespoons", false)
            ),
            notes = "Family lunch"
        )
        savedPredictionRepository.savePrediction(samplePrediction)
    }

    fun getMeals(): List<Meal> = runBlocking { meals.first() }
    
    fun getIngredients(): List<Ingredient> = runBlocking { ingredients.first() }

    fun login(username: String, password: String): Boolean {
        if (username.isNotBlank() && password.isNotBlank()) {
            currentUser = User(
                id = UUID.randomUUID().toString(),
                username = username,
                email = "$username@example.com",
                password = password
            )
            return true
        }
        return false
    }

    fun register(username: String, email: String, password: String): Boolean {
        if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            currentUser = User(
                id = UUID.randomUUID().toString(),
                username = username,
                email = email,
                password = password
            )
            return true
        }
        return false
    }

    fun addMeal(meal: Meal) {
        mealRepository.addMeal(meal)
    }

    fun addIngredient(ingredient: Ingredient) {
        mealRepository.addIngredient(ingredient)
    }

    fun selectMeal(meal: Meal) {
        selectedMeal = meal
    }

    fun selectMealType(type: String) {
        selectedMealType = type
    }

    fun addPerson(person: Person) {
        people = people + person
    }

    fun removePerson(index: Int) {
        people = people.toMutableList().apply { removeAt(index) }
    }

    fun toggleIngredient(ingredient: Ingredient) {
        // Check if the ingredient is already selected
        val isAlreadySelected = selectedIngredients.any { it.id == ingredient.id }
        
        if (isAlreadySelected) {
            // Remove the ingredient if it's already selected
            selectedIngredients = selectedIngredients.filter { it.id != ingredient.id }
            predictionError = null // Clear any previous errors
        } else {
            // Add the ingredient only if it's not already selected
            if (selectedMeal == null || people.isEmpty()) {
                predictionError = "Please select a meal and add people first"
                return
            }
            
            // Add the ingredient and predict its quantity
            selectedIngredients = selectedIngredients + ingredient
            predictIngredientQuantity(ingredient)
        }
    }

    private fun predictIngredientQuantity(ingredient: Ingredient) {
        viewModelScope.launch {
            isPredicting = true
            predictionError = null
            
            val request = IngredientPredictionRequest(
                foodType = selectedMealType,
                ingredient = ingredient.name,
                numberOfPeople = people.size,
                ageGroups = people.map { it.ageGroup }
            )

            predictionRepository.predictIngredient(request)
                .onSuccess { prediction ->
                    selectedIngredients = selectedIngredients.map {
                        if (it.id == ingredient.id) {
                            it.copy(
                                predictedQuantity = prediction.quantity,
                                predictedUnit = prediction.unit
                            )
                        } else {
                            it
                        }
                    }
                }
                .onFailure { error ->
                    predictionError = error.message ?: "Failed to predict ingredient quantity"
                }

            isPredicting = false
        }
    }

    fun resetSession() {
        currentUser = null
        selectedMeal = null
        selectedMealType = ""
        people = emptyList()
        selectedIngredients = emptyList()
        predictionError = null
    }

    fun savePrediction(notes: String = "") {
        if (selectedMeal == null || selectedIngredients.isEmpty()) return

        val prediction = SavedPrediction(
            id = UUID.randomUUID().toString(),
            mealName = selectedMeal!!.name,
            mealType = selectedMealType,
            numberOfPeople = people.size,
            ageGroups = people.map { it.ageGroup },
            ingredients = selectedIngredients.map { ingredient ->
                SavedIngredient(
                    name = ingredient.name,
                    quantity = ingredient.predictedQuantity ?: 0.0,
                    unit = ingredient.predictedUnit ?: "units",
                    isCustomQuantity = false
                )
            },
            notes = notes
        )

        savedPredictionRepository.savePrediction(prediction)
    }

    fun loadSavedPrediction(predictionId: String) {
        val prediction = savedPredictionRepository.getPrediction(predictionId)
        if (prediction != null) {
            resetSession()

            // Load meal
            val meal = runBlocking { meals.first() }.find { it.name == prediction.mealName }
            selectedMeal = meal
            selectedMealType = prediction.mealType

            // Load people
            prediction.ageGroups.forEach { ageGroup ->
                val defaultAge = when (ageGroup) {
                    "Child" -> 10
                    "Teen" -> 16
                    "Adult" -> 35
                    "Elderly" -> 70
                    else -> 35
                }
                
                addPerson(Person(
                    id = UUID.randomUUID().toString(),
                    age = defaultAge,
                    gender = "Not Specified",
                    ageGroup = ageGroup
                ))
            }

            // Load ingredients with their predicted quantities
            val availableIngredients = runBlocking { ingredients.first() }
            prediction.ingredients.forEach { savedIngredient ->
                val ingredient = availableIngredients.find { it.name == savedIngredient.name }
                ingredient?.let {
                    selectedIngredients = selectedIngredients + it.copy(
                        predictedQuantity = savedIngredient.quantity,
                        predictedUnit = savedIngredient.unit
                    )
                }
            }
        }
    }

    fun updatePrediction(updatedPrediction: SavedPrediction) {
        savedPredictionRepository.updatePrediction(updatedPrediction)
    }

    fun updatePredictionIngredients(predictionId: String, updatedIngredients: List<SavedIngredient>) {
        val prediction = savedPredictionRepository.getPrediction(predictionId)
        if (prediction != null) {
            val updatedPrediction = prediction.copy(
                ingredients = updatedIngredients,
                timestamp = System.currentTimeMillis()
            )
            savedPredictionRepository.updatePrediction(updatedPrediction)
        }
    }

    fun deleteSavedPrediction(predictionId: String) {
        savedPredictionRepository.deletePrediction(predictionId)
    }
}