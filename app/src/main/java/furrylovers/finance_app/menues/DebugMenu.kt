package furrylovers.finance_app.menues

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import furrylovers.finance_app.data_save.Data
import furrylovers.finance_app.data_save.DebugSettings
import furrylovers.finance_app.data_save.JsonData
import furrylovers.finance_app.ui.theme.*

class AdultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainTheme {
                AdultMenu()
            }
        }
    }
}

@Composable
fun AdultMenu() {
    val context = LocalContext.current
    val activity = context as? Activity
    val data = remember { JsonData(context).loadData() }

    var intervalText by remember {
        mutableStateOf(if (DebugSettings.finishDayIntervalMs <= 3000L) "Ускоренно (2 сек) 🔥" else "Стандартный (12 часов)")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundWarm)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "👨‍👩‍👧 Кабинет родителей и Статистика",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkChocolate,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Карточка статистики расходов
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AppCreamPanel),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📊 Статистика расходов",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolate
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatRow("Всего потрачено в магазине:", "${data.monetSpened} F")
                    StatRow("Обязательные товары (Еда/Уход):", "${data.expenses.getOrElse(0) { 0 } + data.expenses.getOrElse(1) { 0 }} F")
                    StatRow("Необязательные (Аксессуары):", "${data.expenses.getOrElse(2) { 0 }} F")
                }
            }

            // Карточка накоплений и квестов
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🐷 Накопления и прогресс",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolate
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatRow("Всего отложено в копилку:", "${data.bank} F")
                    StatRow("Завершено квестов:", "${data.questCompleted}")
                }
            }

            // Отладочная кнопка для изменения интервала
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AppCreamCapsule),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚙️ Отладка таймера",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolate
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Текущий режим: $intervalText",
                        fontSize = 12.sp,
                        color = TextMediumBrown
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (DebugSettings.finishDayIntervalMs > 3000L) {
                                DebugSettings.finishDayIntervalMs = 2000L
                                intervalText = "Ускоренно (2 сек) 🔥"
                                Toast.makeText(context, "Интервал «Завершить день» изменён на 2 сек!", Toast.LENGTH_SHORT).show()
                            } else {
                                DebugSettings.finishDayIntervalMs = 12 * 60 * 60 * 1000L
                                intervalText = "Стандартный (12 часов)"
                                Toast.makeText(context, "Интервал возвращен к 12 часам", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BuyButtonGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Переключить на 2 сек", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Нижние кнопки управления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    context.startActivity(Intent(context, TitleScreenActivity::class.java))
                    activity?.finish()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppCreamCapsule,
                    contentColor = TextMediumBrown
                )
            ) {
                Text(text = "Вернуться", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    JsonData(context).deleteData()
                    context.startActivity(Intent(context, CharacterNameActivity::class.java))
                    activity?.finish()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NotificationErrorBadge,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Сбросить", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMediumBrown, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 14.sp, color = DarkChocolate, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9)
@Composable
fun AdultPreview() {
    MainTheme {
        AdultMenu()
    }
}
