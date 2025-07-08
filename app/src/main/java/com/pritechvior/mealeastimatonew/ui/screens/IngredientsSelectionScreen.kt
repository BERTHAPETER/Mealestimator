package com.pritechvior.mealeastimatonew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pritechvior.mealeastimatonew.data.model.TanzanianDishes
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsSelectionScreen(
    onContinue: () -> Unit,
    viewModel: MealEstimatorViewModel
) {
    val selectedMeal = viewModel.selectedMeal
    var showAddIngredientDialog by remember { mutableStateOf(false) }
    var newIngredientName by remember { mutableStateOf("") }
    var selectedIngredients = remember { mutableStateMapOf<String, Double>() }
    
    // Initialize with recommended ingredients when meal changes
    LaunchedEffect(selectedMeal) {
        selectedMeal?.let { meal ->
            // Get recommended ingredients from TanzanianDishes
            val tanzanianDish = TanzanianDishes.dishes.find { 
                it.nameEnglish == meal.name || it.nameSwahili == meal.nameSwahili 
            }
            
            // Initialize with recommended ingredients
            selectedIngredients.clear()
            tanzanianDish?.ingredientsEnglish?.forEach { ingredient ->
                selectedIngredients[ingredient] = 1.0 // Default quantity
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Selected Meal Info
        selectedMeal?.let { meal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${meal.emoji ?: "🍽️"} ${meal.name}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Recommended Ingredients",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedIngredients.entries.toList()) { (ingredient, quantity) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = ingredient,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            // Find and show Swahili name if available
                            val tanzanianDish = TanzanianDishes.dishes.find { 
                                it.nameEnglish == selectedMeal?.name || it.nameSwahili == selectedMeal?.nameSwahili 
                            }
                            val swahiliName = tanzanianDish?.let { dish ->
                                val index = dish.ingredientsEnglish.indexOf(ingredient)
                                if (index != -1) dish.ingredientsSwahili[index] else null
                            }
                            if (swahiliName != null) {
                                Text(
                                    text = swahiliName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Quantity controls
                            IconButton(
                                onClick = {
                                    if (quantity > 0.5) {
                                        selectedIngredients[ingredient] = quantity - 0.5
                                    }
                                }
                            ) {
                                Text("-", fontSize = 20.sp)
                            }
                            
                            Text(
                                text = String.format("%.1f", quantity),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            IconButton(
                                onClick = {
                                    selectedIngredients[ingredient] = quantity + 0.5
                                }
                            ) {
                                Text("+", fontSize = 20.sp)
                            }

                            // Remove ingredient button
                            IconButton(
                                onClick = {
                                    selectedIngredients.remove(ingredient)
                                }
                            ) {
                                Icon(Icons.Default.Delete, "Remove ingredient")
                            }
                        }
                    }
                }
            }
        }

        // Add Ingredient Button
        Button(
            onClick = { showAddIngredientDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.Add, "Add ingredient")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Custom Ingredient")
        }

        // Continue Button
        Button(
            onClick = {
                viewModel.updateSelectedIngredients(selectedIngredients.toMap())
                onContinue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = selectedIngredients.isNotEmpty()
        ) {
            Text("Continue")
        }
    }

    // Add Ingredient Dialog
    if (showAddIngredientDialog) {
        AlertDialog(
            onDismissRequest = { showAddIngredientDialog = false },
            title = { Text("Add Custom Ingredient") },
            text = {
                OutlinedTextField(
                    value = newIngredientName,
                    onValueChange = { newIngredientName = it },
                    label = { Text("Ingredient Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newIngredientName.isNotBlank()) {
                            selectedIngredients[newIngredientName] = 1.0
                            newIngredientName = ""
                            showAddIngredientDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddIngredientDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 