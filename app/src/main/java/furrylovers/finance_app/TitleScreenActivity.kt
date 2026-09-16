package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import furrylovers.finance_app.ui.theme.MainTheme

class TitleScreenActivity : ComponentActivity() { //точка входа 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.PROFILE) } //выбор по умолчанию

    NavigationSuiteScaffold(
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
        when (currentDestination) {
            //тут прописываются действия при нажатии на кнопки снизу
            //тут по идее будем включать функции которые будут выводить целые боксы
            //на экран и там кнопки всякие уже будут и тд
            AppDestinations.HOME -> Greeting(name = AppDestinations.HOME.label)
            AppDestinations.FAVORITES -> Greeting(name = AppDestinations.FAVORITES.label)
            AppDestinations.PROFILE -> Greeting(name = AppDestinations.PROFILE.label)
            AppDestinations.SHOP -> Greeting(name = AppDestinations.SHOP.label)
            AppDestinations.MINIGAMES -> Greeting(name = AppDestinations.MINIGAMES.label)
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
    PROFILE("Profile", R.drawable.ic_account_box),
    SHOP("Shop", R.drawable.ic_favorite),
    MINIGAMES("MiniGames", R.drawable.ic_favorite),
    //вот все эти ебучие R.drawable лежат в папке ./res/drawable и надо сделать будет нормальные иконки и тут их поменять
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    MainMenu()
}