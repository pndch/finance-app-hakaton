package furrylovers.finance_app

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

// Главная функция интерфейса
@Composable
fun MainScreen() {
    val context = LocalContext.current

    Column( // элементы друг под другом Еще есть Row
        modifier = Modifier
            .fillMaxSize()                                       //заполнить весь экран
            .padding(16.dp),                                //отступ от краев
        horizontalAlignment = Alignment.CenterHorizontally,      //выравнивание по горизонтали
        verticalArrangement = Arrangement.Center                 //центрирование объектов
    ) {
        Text(
            text = "Моя говорящая анжела",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = { //Toast - всплывающее сообщение
                Toast.makeText(context, "Кнопка нажата!", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, TitleScreenActivity::class.java))
            }
        ) {
            Text(text = "dildo")
        }
    }
}

// @Preview - визуализация ин тайм
@Preview(showBackground = true)
@Composable
fun Preview() {
    MainScreen()
}