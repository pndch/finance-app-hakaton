package furrylovers.finance_app.menues

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import furrylovers.finance_app.data_save.Data
import furrylovers.finance_app.ui.theme.*

class CharacterNameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    if (!Data().isFirstStart(context)) {
        context.startActivity(Intent(context, TitleScreenActivity::class.java))
        activity?.finish()
    } else {
        MainScreenContent()
    }
}

@Composable
fun MainScreenContent() {
    var characterName by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundWarm)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = AppCreamPanel),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🐾", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Как зовут твоего питомца?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Придумай уникальное имя для своего финансового друга",
                    fontSize = 13.sp,
                    color = TextMediumBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = characterName,
                    onValueChange = { characterName = it },
                    placeholder = { Text("Имя питомца...", color = TextMutedBrown) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkChocolate,
                        unfocusedBorderColor = TextMutedBrown,
                        focusedTextColor = DarkChocolate,
                        unfocusedTextColor = DarkChocolate,
                        cursorColor = DarkChocolate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (characterName.isNotBlank()) {
                            Data().changePetName(context, characterName)
                            context.startActivity(Intent(context, CharacterDesignActivity::class.java))
                            activity?.finish()
                        }
                    },
                    enabled = characterName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BuyButtonGreen,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "Продолжить",
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
fun Preview() {
    MainTheme {
        MainScreenContent()
    }
}
