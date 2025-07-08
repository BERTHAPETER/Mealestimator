package com.pritechvior.mealeastimatonew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
fun IngredientsScreen(
    onContinue: () -> Unit,
    viewModel: MealEstimatorViewModel
) {
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Get all available ingredients from TanzanianDishes
    val allAvailableIngredients = remember {
        TanzanianDishes.dishes.flatMap { it.ingredientsEnglish }.distinct().sorted()
    }
    
    // Use ViewModel's selected ingredients
    val selectedIngredients = viewModel.selectedIngredientNames
    val selectedMeal = viewModel.selectedMeal
    
    // Initialize with ingredients from TanzanianDishes for the selected meal
    LaunchedEffect(selectedMeal) {
        selectedMeal?.let { meal ->
            // Find the corresponding TanzanianDish
            val tanzanianDish = TanzanianDishes.dishes.find { 
                it.nameEnglish == meal.name || it.nameSwahili == meal.nameSwahili 
            }
            
            // Initialize selected ingredients from the TanzanianDish if not already set
            if (selectedIngredients.isEmpty()) {
                val ingredientsToAdd = tanzanianDish?.ingredientsEnglish ?: emptyList()
                val ingredientsMap = ingredientsToAdd.associateWith { 1.0 }
                viewModel.updateSelectedIngredients(ingredientsMap)
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
                        text = "${meal.emoji} ${meal.name}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "People: ${viewModel.people.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Meal Type: ${viewModel.selectedMealType}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Header with Add Ingredient button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Recommended Ingredients",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Remove unwanted or add more ingredients",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Button(
                onClick = { showSearchDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, "Add ingredient", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 14.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show all selected ingredients
            val allSelectedIngredients = selectedIngredients.toList()
            
            if (allSelectedIngredients.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No ingredients selected",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Tap 'Add More' to add ingredients",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(allSelectedIngredients) { ingredient ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ingredient,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                // Show Swahili name
                                selectedMeal?.let { meal ->
                                    val tanzanianDish = TanzanianDishes.dishes.find { 
                                        it.nameEnglish == meal.name || it.nameSwahili == meal.nameSwahili 
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
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Remove ingredient button
                                IconButton(
                                    onClick = {
                                        viewModel.removeIngredient(ingredient)
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, "Remove ingredient")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Calculate Button
        Button(
            onClick = {
                onContinue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(50.dp),
            enabled = true
        ) {
            Text("Calculate Portions")
        }
    }

    // Search Dialog
    if (showSearchDialog) {
        AlertDialog(
            onDismissRequest = { showSearchDialog = false },
            title = { Text("Add Ingredient") },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search ingredients") },
                        leadingIcon = { Icon(Icons.Default.Search, "Search") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Show filtered ingredients
                    val filteredIngredients = allAvailableIngredients.filter { 
                        it.contains(searchQuery, ignoreCase = true) && 
                        !selectedIngredients.contains(it)
                    }
                    
                    LazyColumn(
                        modifier = Modifier.height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredIngredients) { ingredient ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.addIngredient(ingredient)
                                        searchQuery = ""
                                        showSearchDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = ingredient,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}