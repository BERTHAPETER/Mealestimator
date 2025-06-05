import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B35),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFF8A65),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF4CAF50),
    onSecondary = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF6B35),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0DB),
    onPrimaryContainer = Color(0xFF8B0000),
    secondary = Color(0xFF4CAF50),
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    background = Color(0xFFFFFBFF),
    onBackground = Color.Black
)

@Composable
fun MealEstimatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}