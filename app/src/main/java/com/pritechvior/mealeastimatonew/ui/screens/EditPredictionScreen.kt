package com.pritechvior.mealeastimatonew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pritechvior.mealeastimatonew.data.model.Person
import com.pritechvior.mealeastimatonew.data.model.SavedIngredient
import com.pritechvior.mealeastimatonew.data.model.SavedPrediction
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPredictionScreen(
    predictionId: String,
    onBack: () -> Unit,
    viewModel: MealEstimatorViewModel
) {
    val prediction = viewModel.savedPredictions.collectAsState().value
        .find { it.id == predictionId } ?: return

    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showEditIngredientsDialog by remember { mutableStateOf(false) }
    var editedNotes by remember { mutableStateOf(prediction.notes) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Prediction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Meal Info Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Meal Information",
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = prediction.mealName,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = prediction.mealType,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // People Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "People",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )
                            }
                            FilledTonalButton(
                                onClick = { showAddPersonDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Person",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        viewModel.people.forEach { person ->
                            PersonCard(
                                person = person,
                                onDelete = { viewModel.removePerson(viewModel.people.indexOf(person)) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Ingredients Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Ingredients",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )
                            }
                            FilledTonalButton(
                                onClick = { showEditIngredientsDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Ingredients",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        prediction.ingredients.forEach { ingredient ->
                            Text(
                                text = "• ${ingredient.name}: ${ingredient.quantity} ${ingredient.unit}",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Notes Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Notes",
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedNotes,
                            onValueChange = { editedNotes = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Add notes about this prediction...") },
                            minLines = 3
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = {
                        // Save changes and go back
                        viewModel.updatePrediction(
                            prediction.copy(
                                notes = editedNotes,
                                numberOfPeople = viewModel.people.size,
                                ageGroups = viewModel.people.map { it.ageGroup }
                            )
                        )
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes")
                }
            }
        }
    }

    if (showAddPersonDialog) {
        AddPersonDialog(
            onDismiss = { showAddPersonDialog = false },
            onPersonAdded = { person ->
                viewModel.addPerson(person)
                showAddPersonDialog = false
            }
        )
    }

    if (showEditIngredientsDialog) {
        EditIngredientsDialog(
            ingredients = prediction.ingredients,
            onDismiss = { showEditIngredientsDialog = false },
            onSave = { updatedIngredients ->
                viewModel.updatePredictionIngredients(predictionId, updatedIngredients)
                showEditIngredientsDialog = false
            }
        )
    }
}

@Composable
private fun AddPersonDialog(
    onDismiss: () -> Unit,
    onPersonAdded: (Person) -> Unit
) {
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Person") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Male", "Female").forEach { option ->
                            FilterChip(
                                selected = gender == option,
                                onClick = { gender = option },
                                label = { Text(option) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    if (ageInt != null && ageInt > 0) {
                        onPersonAdded(
                            Person(
                                id = UUID.randomUUID().toString(),
                                age = ageInt,
                                gender = gender,
                                ageGroup = Person.getAgeGroup(ageInt)
                            )
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditIngredientsDialog(
    ingredients: List<SavedIngredient>,
    onDismiss: () -> Unit,
    onSave: (List<SavedIngredient>) -> Unit
) {
    var editedIngredients by remember { mutableStateOf(ingredients) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Ingredients") },
        text = {
            LazyColumn {
                items(editedIngredients) { ingredient ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = ingredient.name,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ingredient.quantity.toString(),
                            onValueChange = { newValue ->
                                val newQuantity = newValue.toDoubleOrNull() ?: ingredient.quantity
                                editedIngredients = editedIngredients.map {
                                    if (it.name == ingredient.name) {
                                        it.copy(quantity = newQuantity)
                                    } else {
                                        it
                                    }
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        Text(ingredient.unit)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(editedIngredients) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PersonCard(
    person: Person,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${person.age} years old",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${person.gender} • ${person.ageGroup}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Person",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} 