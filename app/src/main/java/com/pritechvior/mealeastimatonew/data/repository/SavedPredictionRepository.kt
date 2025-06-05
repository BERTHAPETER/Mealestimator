package com.pritechvior.mealeastimatonew.data.repository

import com.pritechvior.mealeastimatonew.data.model.SavedIngredient
import com.pritechvior.mealeastimatonew.data.model.SavedPrediction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SavedPredictionRepository {
    private val _savedPredictions = MutableStateFlow<List<SavedPrediction>>(emptyList())
    val savedPredictions: StateFlow<List<SavedPrediction>> = _savedPredictions.asStateFlow()

    fun savePrediction(prediction: SavedPrediction) {
        _savedPredictions.update { currentList ->
            // If prediction with same ID exists, replace it
            val existingIndex = currentList.indexOfFirst { it.id == prediction.id }
            if (existingIndex >= 0) {
                currentList.toMutableList().apply {
                    set(existingIndex, prediction.copy(timestamp = System.currentTimeMillis()))
                }
            } else {
                currentList + prediction.copy(timestamp = System.currentTimeMillis())
            }
        }
    }

    fun updatePrediction(updatedPrediction: SavedPrediction) {
        _savedPredictions.update { currentList ->
            currentList.map { prediction ->
                if (prediction.id == updatedPrediction.id) {
                    updatedPrediction.copy(timestamp = System.currentTimeMillis())
                } else {
                    prediction
                }
            }
        }
    }

    fun updatePredictionIngredients(predictionId: String, updatedIngredients: List<SavedIngredient>) {
        _savedPredictions.update { currentList ->
            currentList.map { prediction ->
                if (prediction.id == predictionId) {
                    prediction.copy(
                        ingredients = updatedIngredients,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    prediction
                }
            }
        }
    }

    fun getPrediction(id: String): SavedPrediction? {
        return _savedPredictions.value.find { it.id == id }
    }

    fun deletePrediction(id: String) {
        _savedPredictions.update { currentList ->
            currentList.filter { it.id != id }
        }
    }

    fun getAllPredictions(): List<SavedPrediction> {
        return _savedPredictions.value
    }
} 