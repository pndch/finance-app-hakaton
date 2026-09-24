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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import java.nio.file.WatchEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf

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
    CharacterDesignContent()
}

@Composable
fun CharacterDesignContent() {
    var currentCharacter: Int by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(all = 16.dp)
//            .padding(top = 96.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(R.drawable.background_chardesign),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            contentAlignment = Alignment.Center
        )
        {
            Image(
                painter = painterResource(characters[currentCharacter]),
                contentDescription = null,
                modifier = Modifier
                    .height(500.dp)
                    .width(150.dp)
                    .padding(bottom = 100.dp),
                contentScale = ContentScale.FillBounds
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 448.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Button(
                modifier = Modifier
                    .padding(start = 15.dp),

                onClick = {
                    currentCharacter = if ( currentCharacter == 0) {
                        characters.size - 1
                    } else {
                        currentCharacter - 1
                    }
                }
            ) {
                Text(text = "<")
            }

            Button(
                modifier = Modifier
                    .padding(end = 15.dp),

                onClick = {
                    currentCharacter = if ( currentCharacter == characters.size - 1) {
                        0
                    } else {
                        currentCharacter + 1
                    }
                }
            ) {
                Text(text = ">")
            }
        }
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
                Data().changePetType(context, currentCharacter)
                Data().finishFirstStart(context)
                context.startActivity(Intent(context, TitleScreenActivity::class.java))
                activity?.finish()
            }
        ) {
            Text(text = "Сохранить")
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9)
@Composable
fun CharacterDesignPreview() {
    MainTheme {
        CharacterDesignContent()
    }
}