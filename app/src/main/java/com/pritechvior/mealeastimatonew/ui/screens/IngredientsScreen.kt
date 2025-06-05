package com.pritechvior.mealeastimatonew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pritechvior.mealeastimatonew.data.model.Ingredient
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    onContinue: () -> Unit,
    viewModel: MealEstimatorViewModel
) {
    val ingredients = viewModel.getIngredients()
    val selectedIngredients = viewModel.selectedIngredients
    val selectedMeal = viewModel.selectedMeal

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showNoResultsMessage by remember { mutableStateOf(false) }

    val filteredIngredients = remember(ingredients, searchQuery) {
        if (searchQuery.isEmpty()) {
            ingredients
        } else {
            ingredients.filter { 
                it.name.lowercase().contains(searchQuery.lowercase()) 
            }
        }
    }

    // Update showNoResultsMessage when search results change
    LaunchedEffect(searchQuery, filteredIngredients) {
        showNoResultsMessage = searchQuery.isNotEmpty() && filteredIngredients.isEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Show selected meal info
        selectedMeal?.let { meal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = meal.emoji ?: "🍽️",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = meal.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Type: ${viewModel.selectedMealType}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Text(
            text = "Select Ingredients",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search or type new ingredient name") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                IconButton(onClick = { 
                    if (searchQuery.isNotBlank()) {
                        showAddDialog = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom ingredient"
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // No results message with "Add" button
        if (showNoResultsMessage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ingredient not found",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add \"$searchQuery\" as new ingredient")
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredIngredients) { ingredient ->
                val isSelected = selectedIngredients.any { it.id == ingredient.id }

                Card(
                    onClick = { viewModel.toggleIngredient(ingredient) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { viewModel.toggleIngredient(ingredient) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Text(
                            text = ingredient.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        )
                    }
                }
            }
        }

        // Selected ingredients count
        if (selectedIngredients.isNotEmpty()) {
            Text(
                text = "${selectedIngredients.size} ingredients selected",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = onContinue,
            enabled = selectedIngredients.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showAddDialog) {
        var newIngredientName by remember { mutableStateOf(searchQuery) }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Ingredient") },
            text = {
                Column {
                    Text(
                        text = "Add your own ingredient to the recipe",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = newIngredientName,
                        onValueChange = { newIngredientName = it },
                        label = { Text("Ingredient Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newIngredientName.isNotBlank()) {
                            val newIngredient = Ingredient(
                                id = UUID.randomUUID().toString(),
                                name = newIngredientName.trim(),
                                preparationTime = 10 // default prep time
                            )
                            viewModel.addIngredient(newIngredient)
                            viewModel.toggleIngredient(newIngredient)
                            searchQuery = "" // Clear search after adding
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}