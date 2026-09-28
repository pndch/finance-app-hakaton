package furrylovers.finance_app.menues

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import furrylovers.finance_app.data_save.Data
import furrylovers.finance_app.data_save.characters
import furrylovers.finance_app.ui.theme.*

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
    var currentCharacter by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundWarm)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Верхний заголовок
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "Выбери своего питомца",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Питомец будет расти и помогать тебе",
                    fontSize = 13.sp,
                    color = TextMediumBrown,
                    textAlign = TextAlign.Center
                )
            }

            // Центральная карточка с персонажем и кнопками < >
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCreamPanel),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(characters[currentCharacter]),
                            contentDescription = null,
                            modifier = Modifier.size(260.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Кнопка влево
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                ) {
                    Button(
                        onClick = {
                            currentCharacter = if (currentCharacter == 0) {
                                characters.size - 1
                            } else {
                                currentCharacter - 1
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkChocolate,
                            contentColor = CoinGold
                        ),
                        modifier = Modifier.size(50.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "‹", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Кнопка вправо
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                ) {
                    Button(
                        onClick = {
                            currentCharacter = if (currentCharacter == characters.size - 1) {
                                0
                            } else {
                                currentCharacter + 1
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkChocolate,
                            contentColor = CoinGold
                        ),
                        modifier = Modifier.size(50.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "›", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Нижняя кнопка сохранения
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        Data().changePetType(context, currentCharacter)
                        Data().finishFirstStart(context)
                        context.startActivity(Intent(context, TitleScreenActivity::class.java))
                        activity?.finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BuyButtonGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Выбрать и начать",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
