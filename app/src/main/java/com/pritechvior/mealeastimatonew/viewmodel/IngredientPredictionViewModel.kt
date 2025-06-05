package com.pritechvior.mealeastimatonew.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pritechvior.mealeastimatonew.data.model.IngredientPrediction
import com.pritechvior.mealeastimatonew.data.model.IngredientPredictionRequest
import com.pritechvior.mealeastimatonew.data.repository.IngredientPredictionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PredictionUiState(
    val isLoading: Boolean = false,
    val prediction: IngredientPrediction? = null,
    val error: String? = null
)

class IngredientPredictionViewModel(
    private val repository: IngredientPredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    fun predictIngredient(
        foodType: String,
        ingredient: String,
        numberOfPeople: Int,
        ageGroups: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = PredictionUiState(isLoading = true)
            
            val request = IngredientPredictionRequest(
                foodType = foodType,
                ingredient = ingredient,
                numberOfPeople = numberOfPeople,
                ageGroups = ageGroups
            )

            repository.predictIngredient(request)
                .onSuccess { prediction ->
                    _uiState.value = PredictionUiState(prediction = prediction)
                }
                .onFailure { error ->
                    _uiState.value = PredictionUiState(error = error.message ?: "Unknown error occurred")
                }
        }
    }
} 