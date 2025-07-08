package com.pritechvior.mealeastimatonew.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pritechvior.mealeastimatonew.data.model.*
import com.pritechvior.mealeastimatonew.data.repository.DatabaseRepository
import com.pritechvior.mealeastimatonew.data.repository.IngredientPredictionRepository
import com.pritechvior.mealeastimatonew.data.repository.RepositoryProvider
import com.pritechvior.mealeastimatonew.di.NetworkModule
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import com.pritechvior.mealeastimatonew.util.JsonLoader
import com.pritechvior.mealeastimatonew.network.CohereApiService
import com.pritechvior.mealeastimatonew.network.CohereRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect

class MealEstimatorViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val predictionRepository = IngredientPredictionRepository()
    private val mealRepository = RepositoryProvider.getMealRepository()

    val meals: StateFlow<List<Meal>> = mealRepository.meals
    val ingredients: StateFlow<List<Ingredient>> = mealRepository.ingredients

    // SQLite-backed saved predictions
    private val _savedPredictions = MutableStateFlow<List<SavedPrediction>>(emptyList())
    val savedPredictions: StateFlow<List<SavedPrediction>> = _savedPredictions.asStateFlow()

    var currentUser by mutableStateOf<User?>(null)
        private set

    private val _selectedMeal = mutableStateOf<Meal?>(null)
    val selectedMeal: Meal? get() = _selectedMeal.value

    private val _selectedMealType = mutableStateOf<String>("")
    val selectedMealType: String get() = _selectedMealType.value

    private val _people = mutableStateListOf<Person>()
    val people: List<Person> get() = _people

    private val _selectedIngredients = mutableStateMapOf<String, Double>()
    val selectedIngredients: Map<String, Double> get() = _selectedIngredients

    // Add a computed property for just the ingredient names
    val selectedIngredientNames: Set<String> get() = _selectedIngredients.keys.toSet()

    var isPredicting by mutableStateOf(false)
        private set

    var predictionError by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var profileImageUrl by mutableStateOf("")
        private set

    enum class CalculationMethod {
        STANDARD, AI
    }
    var calculationMethod by mutableStateOf(CalculationMethod.STANDARD)

    var aiPredictionResult by mutableStateOf<String?>(null)
    var aiPredictionLoading by mutableStateOf(false)
    var aiPredictionError by mutableStateOf<String?>(null)

    // Add initial data
    init {
        // Load food quantities from JSON file
        val context = databaseRepository.getContext()
        JsonLoader.loadFoodQuantities(context)
        
        // Add traditional Tanzanian meals from TanzanianDishes
        val meals = TanzanianDishes.dishes.map { dish ->
            Meal(
                id = UUID.randomUUID().toString(),
                name = dish.nameEnglish,
                nameSwahili = dish.nameSwahili,
                description = "Traditional Tanzanian ${dish.nameEnglish}",
                emoji = dish.emoji,
                basePreparationTime = 45, // Default preparation time
                difficulty = "Medium",
                defaultIngredients = dish.ingredientsEnglish
            )
        }

        // Add all meals
        meals.forEach { addMeal(it) }

        // Add common ingredients from all Tanzanian dishes
        val allIngredients = TanzanianDishes.dishes
            .flatMap { it.ingredientsEnglish }
            .distinct()
            .map { ingredientName ->
                Ingredient(
                    id = UUID.randomUUID().toString(),
                    name = ingredientName,
                    preparationTime = 10 // Default prep time
                )
            }

        // Add all ingredients
        allIngredients.forEach { addIngredient(it) }
    }

    fun getMeals(): List<Meal> = runBlocking { meals.first() }
    
    fun getIngredients(): List<Ingredient> = runBlocking { ingredients.first() }

    // Update saved predictions when user logs in
    fun refreshSavedPredictions() {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            databaseRepository.getSavedPredictions(userId).collect { predictions ->
                _savedPredictions.value = predictions
            }
        }
    }

    // Call this after login, logout, or saving
    fun login(username: String, password: String): Boolean {
        isLoading = true
        error = null
        return runBlocking {
            val result = databaseRepository.loginUser(username, password)
            result.fold(
                onSuccess = { user ->
                    currentUser = user
                    isLoading = false
                    refreshSavedPredictions()
                    true
                },
                onFailure = {
                    error = it.message ?: "Login failed"
                    isLoading = false
                    false
                }
            )
        }
    }

    fun register(username: String, email: String, password: String): Boolean {
        isLoading = true
        error = null
        return runBlocking {
            val result = databaseRepository.registerUser(username, email, password)
            result.fold(
                onSuccess = { user ->
                    currentUser = user
                    isLoading = false
                    true
                },
                onFailure = {
                    error = it.message ?: "Registration failed"
                    isLoading = false
                    false
                }
            )
        }
    }

    fun updateProfileImage(imageUri: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                val result = databaseRepository.updateUserProfileImage(imageUri)
                result.fold(
                    onSuccess = { url ->
                        profileImageUrl = url
                        isLoading = false
                    },
                    onFailure = {
                        error = it.message ?: "Failed to update profile image"
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                error = e.message ?: "Failed to update profile image"
                isLoading = false
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String): Boolean {
        return runBlocking {
            currentUser?.let { user ->
                // First verify the current password
                if (currentPassword != user.password) {
                    return@runBlocking false
                }

                val result = databaseRepository.updateUserPassword(user.id, newPassword)
                result.fold(
                    onSuccess = { updatedUser ->
                        currentUser = updatedUser
                        true
                    },
                    onFailure = {
                        false
                    }
                )
            } ?: false
        }
    }

    fun addMeal(meal: Meal) {
        mealRepository.addMeal(meal)
    }

    fun addIngredient(ingredient: Ingredient) {
        mealRepository.addIngredient(ingredient)
    }

    fun selectMeal(meal: Meal) {
        _selectedMeal.value = meal
        
        // Find corresponding Tanzanian dish if it exists
        val tanzanianDish = TanzanianDishes.dishes.find { 
            it.nameEnglish == meal.name || it.nameSwahili == meal.nameSwahili 
        }
        
        // Clear existing ingredients
        _selectedIngredients.clear()
        
        // Add default ingredients with initial quantities
        val defaultIngredients = tanzanianDish?.ingredientsEnglish ?: meal.defaultIngredients
        defaultIngredients.forEach { ingredient ->
            _selectedIngredients[ingredient] = 1.0 // Default quantity
        }
    }

    fun getTanzanianDishDetails(mealName: String): TanzanianDish? {
        return TanzanianDishes.dishes.find { 
            it.nameEnglish == mealName || it.nameSwahili == mealName 
        }
    }

    fun setMealType(type: String) {
        _selectedMealType.value = type
    }

    fun addPerson(person: Person) {
        _people.add(person)
    }

    fun removePerson(index: Int) {
        _people.removeAt(index)
    }

    fun updateSelectedIngredients(ingredients: Map<String, Double>) {
        _selectedIngredients.clear()
        _selectedIngredients.putAll(ingredients)
    }

    fun updateSelectedIngredientNames(ingredientNames: List<String>) {
        _selectedIngredients.clear()
        ingredientNames.forEach { name ->
            _selectedIngredients[name] = 1.0 // Default quantity for calculation
        }
    }

    fun addIngredient(ingredientName: String) {
        _selectedIngredients[ingredientName] = 1.0
    }

    fun removeIngredient(ingredientName: String) {
        _selectedIngredients.remove(ingredientName)
    }

    fun getIngredientQuantity(ingredient: String): Double {
        return _selectedIngredients[ingredient] ?: 0.0
    }

    fun toggleIngredient(ingredient: Ingredient) {
        // Check if the ingredient is already selected
        val isAlreadySelected = _selectedIngredients.any { it.key == ingredient.name }
        
        if (isAlreadySelected) {
            // Remove the ingredient if it's already selected
            _selectedIngredients.remove(ingredient.name)
            predictionError = null // Clear any previous errors
        } else {
            // Add the ingredient only if it's not already selected
            if (_selectedMeal.value == null || _people.isEmpty()) {
                predictionError = "Please select a meal and add people first"
                return
            }
            
            // Add the ingredient and predict its quantity
            _selectedIngredients[ingredient.name] = 0.0
            predictIngredientQuantity(ingredient)
        }
    }

    private fun predictIngredientQuantity(ingredient: Ingredient) {
        viewModelScope.launch {
            isPredicting = true
            predictionError = null
            
            val request = IngredientPredictionRequest(
                foodType = _selectedMealType.value,
                ingredient = ingredient.name,
                numberOfPeople = _people.size,
                ageGroups = _people.map { it.ageGroup }
            )

            predictionRepository.predictIngredient(request)
                .onSuccess { prediction ->
                    _selectedIngredients[ingredient.name] = prediction.quantity
                }
                .onFailure { error ->
                    predictionError = error.message ?: "Failed to predict ingredient quantity"
                }

            isPredicting = false
        }
    }

    fun resetSession() {
        currentUser = null
        _selectedMeal.value = null
        _selectedMealType.value = ""
        _people.clear()
        _selectedIngredients.clear()
        predictionError = null
    }

    fun savePrediction(notes: String = "") {
        if (_selectedMeal.value == null || _selectedIngredients.isEmpty()) return
        val calculator = com.pritechvior.mealeastimatonew.util.PortionCalculator
        val supportedIngredients = calculator.getSupportedIngredients()
        val ageGroups = _people.map { it.ageGroup }
        val prediction = com.pritechvior.mealeastimatonew.data.model.SavedPrediction(
            id = java.util.UUID.randomUUID().toString(),
            mealName = _selectedMeal.value!!.name,
            mealType = _selectedMealType.value,
            numberOfPeople = _people.size,
            ageGroups = _people.map { it.ageGroup },
            ingredients = _selectedIngredients.entries.map { (name, quantity) ->
                val (realQuantity, realUnit) = if (supportedIngredients.contains(name)) {
                    calculator.calculateIngredientQuantity(
                        ingredient = name,
                        numberOfPeople = _people.size,
                        ageGroups = ageGroups
                    )
                } else quantity to "units"
                com.pritechvior.mealeastimatonew.data.model.SavedIngredient(
                    name = name,
                    quantity = realQuantity,
                    unit = realUnit,
                    isCustomQuantity = false
                )
            },
            notes = notes
        )
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            databaseRepository.savePrediction(prediction, userId)
            refreshSavedPredictions()
        }
    }

    fun saveCustomPrediction(prediction: com.pritechvior.mealeastimatonew.data.model.SavedPrediction) {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            databaseRepository.savePrediction(prediction, userId)
            refreshSavedPredictions()
        }
    }

    suspend fun loadSavedPrediction(predictionId: String) {
        val userId = currentUser?.id ?: return
        val prediction = databaseRepository.getPredictionById(predictionId)
        if (prediction != null) {
            resetSession()
            // Load meal
            val meal = runBlocking { meals.first() }.find { it.name == prediction.mealName }
            _selectedMeal.value = meal
            _selectedMealType.value = prediction.mealType
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
                    _selectedIngredients[it.name] = savedIngredient.quantity
                }
            }
        }
    }

    fun updatePrediction(updatedPrediction: SavedPrediction) {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            databaseRepository.savePrediction(updatedPrediction, userId)
            refreshSavedPredictions()
        }
    }

    fun updatePredictionIngredients(predictionId: String, updatedIngredients: List<SavedIngredient>) {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            val prediction = databaseRepository.getPredictionById(predictionId)
            if (prediction != null) {
                val updatedPrediction = prediction.copy(
                    ingredients = updatedIngredients,
                    timestamp = System.currentTimeMillis()
                )
                databaseRepository.savePrediction(updatedPrediction, userId)
                refreshSavedPredictions()
            }
        }
    }

    fun deleteSavedPrediction(predictionId: String) {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            databaseRepository.deleteSavedPrediction(predictionId, userId)
            refreshSavedPredictions()
        }
    }

    fun selectProfileImage() {
        // This will be implemented to handle image selection
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                // The actual implementation will be added when we set up image picking
                isLoading = false
            } catch (e: Exception) {
                error = e.message ?: "Failed to select profile image"
                isLoading = false
            }
        }
    }

    fun fetchAIPrediction(prompt: String, apiKey: String) {
        aiPredictionLoading = true
        aiPredictionError = null
        aiPredictionResult = null
        val service = CohereApiService.create(apiKey)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.generate(CohereRequest(prompt = prompt))
                aiPredictionResult = response.generations.firstOrNull()?.text?.trim()
            } catch (e: Exception) {
                aiPredictionError = e.localizedMessage ?: "Unknown error"
            } finally {
                aiPredictionLoading = false
            }
        }
    }

    fun isDuplicatePrediction(newPrediction: SavedPrediction): Boolean {
        return _savedPredictions.value.any { existing ->
            existing.mealName == newPrediction.mealName &&
            existing.mealType == newPrediction.mealType &&
            existing.numberOfPeople == newPrediction.numberOfPeople &&
            existing.ageGroups == newPrediction.ageGroups &&
            existing.ingredients == newPrediction.ingredients
        }
    }
}