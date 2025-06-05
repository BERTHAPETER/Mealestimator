package com.pritechvior.mealeastimatonew.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pritechvior.mealeastimatonew.ui.screens.*
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel

// Bottom Navigation Items
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: String
) {
    object MealSelection : BottomNavItem("meal_selection", "Meals", "🍽️")
    object MealType : BottomNavItem("meal_type", "Type", "📋")
    object PeopleDetails : BottomNavItem("people_details", "People", "👥")
    object Results : BottomNavItem("prediction_results", "Results", "📊")
    object SavedPredictions : BottomNavItem("saved_predictions", "Saved", "💾")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: MealEstimatorViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Routes that should show bottom navigation
    val bottomNavRoutes = listOf(
        "meal_selection",
        "meal_type",
        "people_details",
        "ingredients",
        "prediction_results",
        "saved_predictions"
    )

    val showBottomNav = currentDestination?.route in bottomNavRoutes
    val showProfileIcon = viewModel.currentUser != null && currentDestination?.route != "profile"
    val currentRoute = currentDestination?.route

    Scaffold(
        topBar = {
            if (showProfileIcon) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                "meal_selection" -> "Select Meal"
                                "meal_type" -> "Meal Time"
                                "people_details" -> "People Details"
                                "ingredients" -> "Ingredients"
                                "prediction_results" -> "Results"
                                "saved_predictions" -> "Saved Meals"
                                else -> "Home Meal Estimator"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        if (currentRoute != "meal_selection") {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate("profile") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination?.route
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "welcome",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("welcome") {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate("auth")
                    }
                )
            }

            composable("auth") {
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate("meal_selection") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable("meal_selection") {
                MealSelectionScreen(
                    onMealSelected = {
                        navController.navigate("meal_type")
                    },
                    viewModel = viewModel
                )
            }

            composable("meal_type") {
                MealTypeScreen(
                    onMealTypeSelected = {
                        navController.navigate("people_details")
                    },
                    viewModel = viewModel
                )
            }

            composable("people_details") {
                PeopleDetailsScreen(
                    onContinue = {
                        navController.navigate("ingredients")
                    },
                    viewModel = viewModel
                )
            }

            composable("ingredients") {
                IngredientsScreen(
                    onContinue = {
                        navController.navigate("prediction_results")
                    },
                    viewModel = viewModel
                )
            }

            composable("prediction_results") {
                PredictionResultsScreen(
                    onStartOver = {
                        viewModel.resetSession()
                        navController.navigate("meal_selection") {
                            popUpTo("meal_selection") { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable("saved_predictions") {
                SavedPredictionsScreen(
                    onEditPrediction = { predictionId ->
                        viewModel.loadSavedPrediction(predictionId)
                        navController.navigate("ingredients")
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentDestination: String?
) {
    NavigationBar {
        val items = listOf(
            BottomNavItem.MealSelection,
            BottomNavItem.MealType,
            BottomNavItem.PeopleDetails,
            BottomNavItem.Results,
            BottomNavItem.SavedPredictions
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Text(text = item.icon, fontSize = 24.sp) },
                label = { Text(text = item.title) },
                selected = currentDestination == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}