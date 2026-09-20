package furrylovers.finance_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


//
// СМЕНИТЬ НАЗВАНИЕ ФАЙЛА НА CharacterNameActivity ИЛИ МОЖНО ДАЖЕ ОБЪЕДЕНИТЬ DESIGN и NAME В ОДНУ АКТИВИТИ
//

class MainActivity : ComponentActivity() { //точка входа в программу
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

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

    //Если персонаж создан сразу скипаем этот экран
    //По хорошему вызывать сразу главный экран и там чекать создан он или нет
    //И если не создан вызывать создание перса но как будто все равно
    if (!Data().isFirstStart(context)) {
        context.startActivity(Intent(context, TitleScreenActivity::class.java))
        activity?.finish()
    }


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
        )
        Button(
            onClick = {
                Data().changePetName(context, characterName)
                context.startActivity(Intent(context, CharacterDesignActivity::class.java))
                activity?.finish()
            }
        ) { Text(text = "Сохранить и продолжить") }
    }
}

// @Preview - визуализация ин тайм
@Preview(showBackground = true)
@Composable
fun Preview() {
    MainScreen()
}
