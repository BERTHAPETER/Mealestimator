package com.pritechvior.mealeastimatonew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pritechvior.mealeastimatonew.util.PortionCalculator
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel
import com.pritechvior.mealeastimatonew.data.model.Person
import com.pritechvior.mealeastimatonew.data.model.Meal
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MealEstimatorViewModel,
    onBackClick: () -> Unit,
    onSaveClick: (() -> Unit)? = null // Make optional for backward compatibility
) {
    val context = LocalContext.current
    val apiKey = "gfOjBrf3g2CrFCjqOxSt8ENkGolFL00bCHpolxUz"
    val people = viewModel.people
    val selectedMeal = viewModel.selectedMeal
    val selectedIngredients = viewModel.selectedIngredientNames

    // Make the whole page scrollable
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Meal Title at the Top
        selectedMeal?.let { meal ->
            Text(
                text = "${meal.emoji} ${meal.name}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Meal Info Card
        selectedMeal?.let { meal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "People: ${people.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Meal Type: ${viewModel.selectedMealType}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Selected Ingredients: ${selectedIngredients.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Calculation Method Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val methods = listOf(
                MealEstimatorViewModel.CalculationMethod.STANDARD to "Standard",
                MealEstimatorViewModel.CalculationMethod.AI to "AI-Assisted"
            )
            methods.forEach { (method, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = viewModel.calculationMethod == method,
                        onClick = { viewModel.calculationMethod = method }
                    )
                    Text(label)
                }
            }
        }

        // Results
        when (viewModel.calculationMethod) {
            MealEstimatorViewModel.CalculationMethod.STANDARD -> {
                if (selectedIngredients.isEmpty()) {
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Please go back and select ingredients",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    selectedIngredients.toList().forEach { ingredient ->
                        val calculator = PortionCalculator
                        val ageGroups = people.map { it.ageGroup }
                        val supportedIngredients = calculator.getSupportedIngredients()
                        if (supportedIngredients.contains(ingredient)) {
                            val (quantity, unit) = calculator.calculateIngredientQuantity(
                                ingredient = ingredient,
                                numberOfPeople = people.size,
                                ageGroups = ageGroups
                            )
                            val hasNutritionalData = calculator.calculateNutrition(
                                ingredient = ingredient,
                                quantity = quantity,
                                unit = unit,
                                ageGroups = ageGroups
                            ) != Triple(0.0, 0.0, 0.0)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (hasNutritionalData)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = ingredient,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasNutritionalData)
                                            MaterialTheme.colorScheme.onSecondaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Estimated Amount: $quantity $unit",
                                        color = if (hasNutritionalData)
                                            MaterialTheme.colorScheme.onSecondaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (hasNutritionalData) {
                                        val (energy, protein, fat) = calculator.calculateNutrition(
                                            ingredient = ingredient,
                                            quantity = quantity,
                                            unit = unit,
                                            ageGroups = ageGroups
                                        )
                                        Text(
                                            text = "Nutritional Information:",
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = "• Energy: $energy kcal",
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = "• Protein: $protein g",
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = "• Fat: $fat g",
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    } else {
                                        Text(
                                            text = "✅ Portion estimated based on standard serving sizes",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Nutritional data not available for this ingredient",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = ingredient,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⚠️ No portion data available for this ingredient",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "This ingredient is not in our database yet.",
                                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            MealEstimatorViewModel.CalculationMethod.AI -> {
                val prompt = buildCoherePrompt(people, selectedIngredients, selectedMeal)
                LaunchedEffect(prompt) {
                    if (!viewModel.aiPredictionLoading && viewModel.aiPredictionResult == null && viewModel.aiPredictionError == null) {
                        viewModel.fetchAIPrediction(prompt, apiKey)
                    }
                }
                when {
                    viewModel.aiPredictionLoading -> {
                        Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("AI-Assisted Prediction (Cohere)", fontWeight = FontWeight.Bold)
                                Text("Loading...")
                            }
                        }
                    }
                    viewModel.aiPredictionError != null -> {
                        Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("AI-Assisted Prediction (Cohere)", fontWeight = FontWeight.Bold)
                                Text("Error: ${viewModel.aiPredictionError}")
                            }
                        }
                    }
                    viewModel.aiPredictionResult != null -> {
                        Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("AI-Assisted Prediction (Cohere)", fontWeight = FontWeight.Bold)
                                Text(viewModel.aiPredictionResult!!)
                            }
                        }
                    }
                }
            }
        }

        // Save Result Button at the bottom
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val method = viewModel.calculationMethod
                var success = false
                var duplicate = false
                if (method == MealEstimatorViewModel.CalculationMethod.STANDARD) {
                    // Build the prediction as in savePrediction, but don't save yet
                    val calculator = PortionCalculator
                    val supportedIngredients = calculator.getSupportedIngredients()
                    val ageGroups = people.map { it.ageGroup }
                    val prediction = com.pritechvior.mealeastimatonew.data.model.SavedPrediction(
                        id = java.util.UUID.randomUUID().toString(),
                        mealName = selectedMeal?.name ?: "",
                        mealType = viewModel.selectedMealType,
                        numberOfPeople = people.size,
                        ageGroups = people.map { it.ageGroup },
                        ingredients = selectedIngredients.map { name ->
                            val (realQuantity, realUnit) = if (supportedIngredients.contains(name)) {
                                calculator.calculateIngredientQuantity(
                                    ingredient = name,
                                    numberOfPeople = people.size,
                                    ageGroups = ageGroups
                                )
                            } else 1.0 to "units"
                            com.pritechvior.mealeastimatonew.data.model.SavedIngredient(
                                name = name,
                                quantity = realQuantity,
                                unit = realUnit,
                                isCustomQuantity = false
                            )
                        },
                        notes = ""
                    )
                    if (viewModel.isDuplicatePrediction(prediction)) {
                        duplicate = true
                    } else {
                        viewModel.savePrediction()
                        success = true
                    }
                } else if (method == MealEstimatorViewModel.CalculationMethod.AI) {
                    val aiResult = viewModel.aiPredictionResult
                    if (!aiResult.isNullOrBlank()) {
                        val ingredients = aiResult.lines().mapNotNull { line ->
                            val parts = line.split(":", limit = 2)
                            if (parts.size == 2) {
                                val name = parts[0].trim()
                                val valueUnit = parts[1].trim().split(" ", limit = 2)
                                if (valueUnit.size == 2) {
                                    val amount = valueUnit[0].toDoubleOrNull() ?: 0.0
                                    val unit = valueUnit[1]
                                    com.pritechvior.mealeastimatonew.data.model.SavedIngredient(
                                        name = name,
                                        quantity = amount,
                                        unit = unit,
                                        isCustomQuantity = true
                                    )
                                } else null
                            } else null
                        }
                        if (ingredients.isNotEmpty() && selectedMeal != null) {
                            val prediction = com.pritechvior.mealeastimatonew.data.model.SavedPrediction(
                                id = java.util.UUID.randomUUID().toString(),
                                mealName = selectedMeal.name,
                                mealType = viewModel.selectedMealType,
                                numberOfPeople = people.size,
                                ageGroups = people.map { it.ageGroup },
                                ingredients = ingredients,
                                notes = "Saved from AI-Assisted result"
                            )
                            if (viewModel.isDuplicatePrediction(prediction)) {
                                duplicate = true
                            } else {
                                viewModel.saveCustomPrediction(prediction)
                                success = true
                            }
                        }
                    }
                }
                android.widget.Toast.makeText(
                    context,
                    when {
                        duplicate -> "This result is already saved"
                        success -> "Result saved!"
                        else -> "Failed to save result"
                    },
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                onSaveClick?.invoke()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Save Result")
        }
    }
}

// Build a smart prompt for Cohere
fun buildCoherePrompt(
    people: List<Person>,
    selectedIngredients: Set<String>,
    selectedMeal: Meal?
): String {
    val peopleDesc = people.joinToString { "${it.gender} (${it.age}y, ${it.ageGroup})" }
    val ingredientsDesc = selectedIngredients.joinToString()
    val mealType = selectedMeal?.let { it.description ?: "Unknown" } ?: "Unknown"
    return """
        Predict the amount of raw ingredients needed for a Tanzanian meal.
        Meal: ${selectedMeal?.name ?: "Unknown"}
        Meal Type: $mealType
        People: $peopleDesc
        Ingredients: $ingredientsDesc
        List only the ingredient names and their required amounts in grams or ml. Do not explain your reasoning. Do not include disclaimers. Format your answer as: Ingredient: Amount Unit. Respond only with the list.
    """.trimIndent()
} 