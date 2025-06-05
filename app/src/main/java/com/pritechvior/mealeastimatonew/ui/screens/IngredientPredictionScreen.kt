package com.pritechvior.mealeastimatonew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pritechvior.mealeastimatonew.data.repository.IngredientPredictionRepository
import com.pritechvior.mealeastimatonew.viewmodel.IngredientPredictionViewModel
import com.pritechvior.mealeastimatonew.viewmodel.PredictionUiState

@Composable
fun IngredientPredictionScreen() {
    val viewModel = remember {
        IngredientPredictionViewModel(
            repository = IngredientPredictionRepository()
        )
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    var foodType by remember { mutableStateOf("") }
    var ingredient by remember { mutableStateOf("") }
    var numberOfPeople by remember { mutableStateOf("") }
    var ageGroups by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Show supported ingredients info
        Text(
            text = "Supported ingredients: ${IngredientPredictionRepository().getSupportedIngredients().joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = foodType,
            onValueChange = { foodType = it },
            label = { Text("Food Type (Breakfast/Lunch/Dinner/Snack)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ingredient,
            onValueChange = { ingredient = it },
            label = { Text("Ingredient") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = numberOfPeople,
            onValueChange = { numberOfPeople = it },
            label = { Text("Number of People") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ageGroups,
            onValueChange = { ageGroups = it },
            label = { Text("Age Groups (Child/Teen/Adult/Elderly, comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val people = numberOfPeople.toIntOrNull() ?: 0
                val groups = ageGroups.split(",").map { it.trim() }
                viewModel.predictIngredient(foodType, ingredient, people, groups)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Predict")
        }

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            uiState.error != null -> {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            uiState.prediction != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Prediction Result",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ingredient: ${uiState.prediction!!.ingredient}")
                        Text("Quantity: ${uiState.prediction!!.quantity} ${uiState.prediction!!.unit}")
                    }
                }
            }
        }
    }
} 