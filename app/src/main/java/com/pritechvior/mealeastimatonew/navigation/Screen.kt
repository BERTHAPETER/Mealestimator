sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Auth : Screen("auth")
    object MealType : Screen("meal_type")
    object MealSelection : Screen("meal_selection")
    object People : Screen("people")
    object IngredientsSelection : Screen("ingredients_selection")
    object Result : Screen("result")
    object SavedPredictions : Screen("saved_predictions")
} 