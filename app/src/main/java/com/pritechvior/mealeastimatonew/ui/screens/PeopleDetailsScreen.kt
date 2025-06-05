package com.pritechvior.mealeastimatonew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pritechvior.mealeastimatonew.data.model.Person
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel
import java.util.UUID

data class AgeGroup(
    val name: String,
    val defaultAge: Int,
    val range: String
)

val ageGroups = listOf(
    AgeGroup("Children", 8, "5-12"),
    AgeGroup("Teens", 15, "13-17"),
    AgeGroup("Adults", 35, "18-64"),
    AgeGroup("Elderly", 70, "65+")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleDetailsScreen(
    onContinue: () -> Unit,
    viewModel: MealEstimatorViewModel
) {
    var showGroupAddDialog by remember { mutableStateOf(false) }
    var showDetailedAddDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    
    val people = viewModel.people
    val selectedMeal = viewModel.selectedMeal

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
            text = "Add People",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (showError) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Please add at least one person",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Quick Add Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Add People",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { showGroupAddDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Group")
                    }
                    Button(
                        onClick = { showDetailedAddDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Individual")
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(people) { person ->
                PersonCard(
                    person = person,
                    onDelete = { viewModel.removePerson(people.indexOf(person)) }
                )
            }
        }

        Button(
            onClick = {
                if (people.isEmpty()) {
                    showError = true
                } else {
                    showError = false
                    onContinue()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Continue (${people.size} people)")
        }
    }

    if (showGroupAddDialog) {
        GroupAddDialog(
            onDismiss = { showGroupAddDialog = false },
            onAdd = { groupData ->
                groupData.forEach { (ageGroup, genderCounts) ->
                    genderCounts.forEach { (gender, count) ->
                        repeat(count) {
                            viewModel.addPerson(
                                Person(
                                    id = UUID.randomUUID().toString(),
                                    age = ageGroup.defaultAge,
                                    gender = gender,
                                    ageGroup = ageGroup.name
                                )
                            )
                        }
                    }
                }
                showGroupAddDialog = false
                showError = false
            }
        )
    }

    if (showDetailedAddDialog) {
        DetailedAddDialog(
            onDismiss = { showDetailedAddDialog = false },
            onAdd = { age, gender ->
                viewModel.addPerson(
                    Person(
                        id = UUID.randomUUID().toString(),
                        age = age,
                        gender = gender,
                        ageGroup = Person.getAgeGroup(age)
                    )
                )
                showDetailedAddDialog = false
                showError = false
            }
        )
    }
}

@Composable
private fun GroupAddDialog(
    onDismiss: () -> Unit,
    onAdd: (Map<AgeGroup, Map<String, Int>>) -> Unit
) {
    var groupDataState by remember {
        mutableStateOf(
            ageGroups.associate { ageGroup ->
                ageGroup to mapOf("Male" to 0, "Female" to 0)
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Group") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ageGroups.forEach { ageGroup ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "${ageGroup.name} (${ageGroup.range})",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf("Male", "Female").forEach { gender ->
                                    Column {
                                        Text(
                                            text = gender,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val currentCount = groupDataState[ageGroup]?.get(gender) ?: 0
                                                    if (currentCount > 0) {
                                                        groupDataState = groupDataState.toMutableMap().apply {
                                                            put(ageGroup, (get(ageGroup) ?: emptyMap()).toMutableMap().apply {
                                                                put(gender, currentCount - 1)
                                                            })
                                                        }
                                                    }
                                                }
                                            ) {
                                                Text("-", style = MaterialTheme.typography.titleLarge)
                                            }
                                            Text(
                                                text = "${groupDataState[ageGroup]?.get(gender) ?: 0}",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            IconButton(
                                                onClick = {
                                                    val currentCount = groupDataState[ageGroup]?.get(gender) ?: 0
                                                    groupDataState = groupDataState.toMutableMap().apply {
                                                        put(ageGroup, (get(ageGroup) ?: emptyMap()).toMutableMap().apply {
                                                            put(gender, currentCount + 1)
                                                        })
                                                    }
                                                }
                                            ) {
                                                Text("+", style = MaterialTheme.typography.titleLarge)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Total count display
                val totalCount = groupDataState.values.sumOf { it.values.sum() }
                if (totalCount > 0) {
                    Text(
                        text = "Total: $totalCount people",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val totalCount = groupDataState.values.sumOf { it.values.sum() }
                    if (totalCount > 0) {
                        onAdd(groupDataState)
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

@Composable
private fun DetailedAddDialog(
    onDismiss: () -> Unit,
    onAdd: (age: Int, gender: String) -> Unit
) {
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Individual") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { char -> char.isDigit() } },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        onAdd(ageInt, gender)
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

@Composable
private fun PersonCard(
    person: Person,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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