package furrylovers.finance_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.nio.file.WatchEvent

class MainActivity : ComponentActivity() { //точка входа в программу
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // !json.empty()
            MainScreen() //весь UI запускается отсюда
        }
    }
}

// Главная функция интерфейса
@Composable
fun MainScreen() { //потом поменять название надо будет на что то осмысленное
    val context = LocalContext.current
    var characterName by rememberSaveable { mutableStateOf("") }
    val activity = context as? Activity


    Column( // элементы друг под другом Еще есть Row
        modifier = Modifier
            .fillMaxSize()                                       //заполнить весь экран
            .padding(16.dp),                                //отступ от краев
        horizontalAlignment = Alignment.CenterHorizontally,      //выравнивание по горизонтали
        verticalArrangement = Arrangement.Center                 //центрирование объектов
    ) {
        Text(
            text = "Введите имя персонажа",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = characterName,
            onValueChange = { newText ->
                characterName = newText},
            modifier = Modifier.padding(bottom = 8.dp)
            //label = { Text("Введите текст") }
        )
        Button(
            onClick = {
                //всплывающее сообщение Toast.makeText(context, "Кнопка нажата!", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, CharacterDesignActivity::class.java))
                activity?.finish()

            }
        ) {
            Text(text = "Сохранить")
        }
    }
}

// @Preview - визуализация ин тайм
@Preview(showBackground = true)
@Composable
fun Preview() {
    MainScreen()
}