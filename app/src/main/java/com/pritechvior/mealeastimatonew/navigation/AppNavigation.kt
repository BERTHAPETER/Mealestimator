package com.pritechvior.mealeastimatonew.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pritechvior.mealeastimatonew.ui.screens.*
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModel
import com.pritechvior.mealeastimatonew.viewmodel.MealEstimatorViewModelFactory
import com.pritechvior.mealeastimatonew.navigation.Screen
import com.pritechvior.mealeastimatonew.navigation.BottomNavItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel: MealEstimatorViewModel = viewModel(
        factory = MealEstimatorViewModelFactory(context)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Routes that should show bottom navigation
    val bottomNavRoutes = listOf(
        Screen.MealSelection.route,
        Screen.MealType.route,
        Screen.PeopleDetails.route,
        Screen.Ingredients.route,
        Screen.Results.route,
        Screen.SavedPredictions.route
    )

    val showBottomNav = currentDestination?.route in bottomNavRoutes
    val showProfileIcon = viewModel.currentUser != null && currentDestination?.route != Screen.Profile.route
    val currentRoute = currentDestination?.route

    Scaffold(
        topBar = {
            if (showProfileIcon) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                Screen.MealSelection.route -> "Select Meal"
                                Screen.MealType.route -> "Meal Time"
                                Screen.PeopleDetails.route -> "People Details"
                                Screen.Ingredients.route -> "Select Ingredients"
                                Screen.Results.route -> "Results"
                                Screen.SavedPredictions.route -> "Saved Meals"
                                else -> "Home Meal Estimator"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        if (currentRoute != Screen.MealSelection.route) {
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
                            onClick = { navController.navigate(Screen.Profile.route) }
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
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            composable(Screen.Auth.route) {
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate(Screen.MealType.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.MealType.route) {
                MealTypeScreen(
                    onMealTypeSelected = {
                        navController.navigate(Screen.MealSelection.route)
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.MealSelection.route) {
                MealSelectionScreen(
                    onMealSelected = {
                        navController.navigate(Screen.PeopleDetails.route)
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.PeopleDetails.route) {
                PeopleDetailsScreen(
                    onContinue = {
                        navController.navigate(Screen.Ingredients.route)
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.Ingredients.route) {
                IngredientsScreen(
                    onContinue = {
                        navController.navigate(Screen.Results.route)
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.Results.route) {
                ResultScreen(
                    onBackClick = {
                        navController.navigateUp()
                    },
                    viewModel = viewModel,
                    onSaveClick = { viewModel.savePrediction() }
                )
            }

            composable(Screen.SavedPredictions.route) {
                SavedPredictionsScreen(
                    onEditPrediction = { predictionId ->
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.loadSavedPrediction(predictionId)
                            navController.navigate(Screen.Ingredients.route)
                        }
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    currentDestination: String?
) {
    NavigationBar {
        val items = listOf(
            BottomNavItem.MealType,
            BottomNavItem.MealSelection,
            BottomNavItem.PeopleDetails,
            BottomNavItem.Ingredients,
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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
} 