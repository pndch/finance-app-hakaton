package furrylovers.finance_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
private val DarkColorScheme = darkColorScheme(
    background = BackgroundWhite,   //задний фон
    surface = SurfaceWhite,         //любые карточки поверх фона

    primary = PrimaryBlue,          //акценты
    secondary = PurpleGrey40,
    tertiary = Pink40,

    onTertiary = Color.Black,
//    background = BackgroundDark,    //задний фон
//    surface = SurfaceDark,          //любые карточки поверх фона
//
//    primary = PrimaryBlue,          //акценты
//    secondary = PurpleGrey80,
//    tertiary = Pink80,
//
//    onTertiary = Color.Black,
)

private val LightColorScheme = lightColorScheme(
    background = BackgroundWhite,   //задний фон
    surface = SurfaceWhite,         //любые карточки поверх фона

    primary = PrimaryBlue,          //акценты
    secondary = PurpleGrey40,
    tertiary = Pink40,

    onTertiary = Color.Black,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}