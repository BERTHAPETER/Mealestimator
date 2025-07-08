package com.pritechvior.mealeastimatonew.navigation

// Define all navigation routes
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Auth : Screen("auth")
    object MealSelection : Screen("meal_selection")
    object MealType : Screen("meal_type")
    object PeopleDetails : Screen("people_details")
    object Ingredients : Screen("ingredients")
    object Results : Screen("results")
    object SavedPredictions : Screen("saved_predictions")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

// Bottom Navigation Items using Screen routes
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: String
) {
    object MealSelection : BottomNavItem(Screen.MealSelection.route, "Meals", "🍽️")
    object MealType : BottomNavItem(Screen.MealType.route, "Type", "📋")
    object PeopleDetails : BottomNavItem(Screen.PeopleDetails.route, "People", "👥")
    object Ingredients : BottomNavItem(Screen.Ingredients.route, "Ingredients", "🥘")
    object Results : BottomNavItem(Screen.Results.route, "Results", "📊")
    object SavedPredictions : BottomNavItem(Screen.SavedPredictions.route, "Saved", "💾")
} 