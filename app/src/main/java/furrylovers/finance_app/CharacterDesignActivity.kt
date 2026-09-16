package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import furrylovers.finance_app.ui.theme.MainTheme
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import java.nio.file.WatchEvent

class CharacterDesignActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainTheme {
                CharacterDesign()
            }
        }
    }
}

@Composable
fun CharacterDesign() {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp), // тут тоже потыкаться надо камера текст перекрывает иногда
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Выберите внешность вашего питомца",
            )
    }

    Column( //в этом контейнере делаем создание персонажа
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello!",
            )
        //хз ну напихать кнопок можно типо ну как я себе это представляю типо вот стоит чучело это
        //и там условно у него на голове две стрелки влево вправо на туловище и на ногах условно
        //и мы выбираем ему не костюм а там какие то его внешние хар-ки типо там шерсть хз чо у нас
        //за фурри будет пока что бля можно вообще фурсьют кароче сделать
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            modifier = Modifier
                .padding(bottom = 24.dp), //тут вот с этим поиграться надо иногда чот она слишком низко и там какие то кнопки устройства перекрывают

            onClick = {
                context.startActivity(Intent(context, TitleScreenActivity::class.java))
            }
        ) {
            Text(text = "Сохранить")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainTheme {
        CharacterDesign()
    }
}