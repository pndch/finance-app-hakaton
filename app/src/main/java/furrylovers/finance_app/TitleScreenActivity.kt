package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import furrylovers.finance_app.ui.theme.MainTheme

class TitleScreenActivity : ComponentActivity() { //точка входа 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setBackgroundDrawableResource(android.R.color.transparent)
        setContent {
            MainTheme() {
                MainMenu()
            }
        }
    }
}

//@PreviewScreenSizes
@Composable
fun MainMenu() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) } //выбор по умолчанию

    AnimatedBackground()

    Box(modifier = Modifier.fillMaxSize()){



        NavigationSuiteScaffold(
            containerColor = Color.Transparent,
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                Color.Transparent
            ),
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                painterResource(it.icon),
                                contentDescription = it.label
                            )
                        },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
//            when (currentDestination) {
//                //тут прописываются действия при нажатии на кнопки снизу
//                //тут по идее будем включать функции которые будут выводить целые боксы
//                //на экран и там кнопки всякие уже будут и тд
//                AppDestinations.HOME -> Greeting(name = AppDestinations.HOME.label)
//                AppDestinations.FAVORITES -> Greeting(name = AppDestinations.FAVORITES.label)
//                AppDestinations.SHOP -> Greeting(name = AppDestinations.SHOP.label)
//                AppDestinations.MINIGAMES -> Greeting(name = AppDestinations.MINIGAMES.label)
//            }
        }


    }

}

enum class AppDestinations( //кнопки внизу экрана
    val label: String,
    val icon: Int,
) {
    //нужно будет подумать над тем чо куда пихать
    HOME("Home", R.drawable.ic_home),
    FAVORITES("Бюджет", R.drawable.ic_favorite),
    SHOP("Shop", R.drawable.ic_favorite),
    MINIGAMES("MiniGames", R.drawable.ic_favorite),
    //вот все эти ебучие R.drawable лежат в папке ./res/drawable и надо сделать будет нормальные иконки и тут их поменять
}


@Composable
fun AnimatedBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "bg")

    // лёгкое покачивание по вертикали
    val offsetY by transition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Image(
        painter = painterResource(R.drawable.background),
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { translationY = offsetY },
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    MainMenu()
}