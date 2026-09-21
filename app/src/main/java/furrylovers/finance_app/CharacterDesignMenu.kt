package furrylovers.finance_app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import furrylovers.finance_app.ui.theme.MainTheme
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Context

class CharacterDesignActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val activity = context as? Activity

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp), // тут тоже потыкаться надо камера текст перекрывает иногда
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Выбери понравившегося питомца",
            )
    }


    //Контейнер с выбором персонажа
    //Добавить нормальные картинки + отредактировать параметры вообщем
    //Логика сделана
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
            .padding(top = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 1) }
            ) {
                Text("1")
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 2)}
            ) {
                Text("2")
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 3)}
            ) {
                Text("3")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 4)}
            ) {
                Text("4")
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 5)}
            ) {
                Text("5")
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 6)}
            ) {
                Text("6")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 7)}
            ) {
                Text("7")
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 8)}
            ) {
                Text("8")
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(100.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { Data().changePetType(context, 9)}
            ) {
                Text("9")
            }
        }

        //
        // ВСТАВИТЬ КАРТИНКУ ВЫБРАННОГО В ДАННЫЙ МОМЕНТ ПЕРСОНАЖА
        //
        Box(
            modifier = Modifier
                .padding(top = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "**Тут вставь картинку выбранного в данный момент перса**")
        }
        //
        // ВСТАВИТЬ КАРТИНКУ ВЫБРАННОГО В ДАННЫЙ МОМЕНТ ПЕРСОНАЖА
        //
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
                Data().finishFirstStart(context)
                context.startActivity(Intent(context, TitleScreenActivity::class.java))
                activity?.finish()
            }
        ) {
            Text(text = "Продолжить")
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