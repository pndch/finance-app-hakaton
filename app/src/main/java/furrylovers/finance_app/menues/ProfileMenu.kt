package furrylovers.finance_app.menues

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import furrylovers.finance_app.data_save.Data
import furrylovers.finance_app.viewmodel.GameViewModel
import furrylovers.finance_app.quests.QuestCategory
import furrylovers.finance_app.data_save.characters
import furrylovers.finance_app.ui.theme.*
import kotlin.math.round

data class PiggyGoal(
    val id: Int,
    val name: String,
    val targetAmount: Int,
    val xpReward: Int,
    val description: String
)

val piggyGoalsList = listOf(
    PiggyGoal(1, "Уютная лежанка", 300, 300, "Удобное место для сна вашего питомца"),
    PiggyGoal(2, "Игровой комплекс", 600, 600, "Большой игровой центр с игрушками"),
    PiggyGoal(3, "Золотой ошейник", 1000, 1000, "Роскошный аксессуар для питомца"),
    PiggyGoal(4, "Загородный дом", 2000, 2000, "Огромный дом мечты!")
)

@Composable
fun ProfileMenu(viewModel: GameViewModel) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    ProfileMenuContent(
        data = data,
        onDepositClick = { amount, target ->
            viewModel.depositToBank(amount, target)
        }
    )
}

@Composable
fun ProfileMenuContent(
    data: Data,
    onDepositClick: (amount: Int, target: Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    var showDepositDialog by remember { mutableStateOf(false) }

    // Расчёт уровня и опыта на основе questAward выполненных квестов и ивентов
    val totalEarnedXp = data.quests
        .filter { it.questStatus == QuestCategory.COMPLETED }
        .sumOf { it.questAward } +
        (if (data.level3EventCompleted) 300 else 0) +
        (if (data.level5EventCompleted) 500 else 0)

    val levelXpThreshold = 300
    val level = data.petStage + (totalEarnedXp / levelXpThreshold)
    val currentXp = totalEarnedXp % levelXpThreshold
    val maxXp = levelXpThreshold
    val remainingXp = maxXp - currentXp
    val xpProgressFraction = (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)

    val animatedXpProgress by animateFloatAsState(
        targetValue = xpProgressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "xpProgress"
    )

    // Расчёт целей
    val goalIndex = (data.petStage - 1).coerceIn(0, piggyGoalsList.size - 1)
    val currentGoal = piggyGoalsList[goalIndex]
    val goalProgressFraction = (data.bank.toFloat() / currentGoal.targetAmount.toFloat()).coerceIn(0f, 1f)

    val animatedGoalProgress by animateFloatAsState(
        targetValue = goalProgressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "goalProgress"
    )

    if (showDepositDialog) {
        DepositDialog(
            currentBalance = data.money,
            goalTarget = currentGoal.targetAmount,
            currentSaved = data.bank,
            onDismiss = { showDepositDialog = false },
            onConfirm = { depositAmount ->
                onDepositClick(depositAmount, currentGoal.targetAmount)
                showDepositDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundWarm)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Заголовок профиля с Аватаром и Именем
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppCreamPanel),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AppCreamCapsule)
                        .border(3.dp, CoinGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(characters[data.petType.coerceIn(0, characters.size - 1)]),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.petName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolate
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkChocolate)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Уровень $level",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoinGold
                            )
                        }

                        val stageTitle = when {
                            level <= 2 -> "Малыш 🍼"
                            level in 3..4 -> "Юный экономист 📚"
                            else -> "Финансист 💼"
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppCreamCapsule)
                                .border(1.dp, CoinGold, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stageTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkChocolate
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Карточка Уровня и Опыта
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ Прогресс уровня",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolate
                    )
                    Text(
                        text = "$currentXp / $maxXp XP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Зеленая шкала опыта
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(AppTrackBg),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (animatedXpProgress > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedXpProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(DarkGreen, SuccessGreenBright, LightGreen)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "До следующего уровня осталось $remainingXp XP",
                    fontSize = 12.sp,
                    color = TextMediumBrown
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Карточка Копилки и Цели Накопления
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯 Цель накопления",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolate
                    )

                    ProfileCoinBadge()
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Детали цели
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppCreamPanel)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppCreamCapsule),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🐷", fontSize = 28.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentGoal.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkBrown
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentGoal.description,
                                fontSize = 12.sp,
                                color = TextMediumBrown
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Накоплено в копилке
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "В копилке:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolate
                    )
                    Text(
                        text = "${data.bank} / ${currentGoal.targetAmount} F",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkChocolate
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Золотистая шкала копилки
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppTrackBg),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (animatedGoalProgress > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedGoalProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(CoinGold, CoinYellowLight)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка пополнения копилки
                Button(
                    onClick = { showDepositDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BuyButtonGreen,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💰", fontSize = 16.sp)
                        Text(
                            text = "Отложить в копилку",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Раздел для взрослых
        Button(
            onClick = {
                context.startActivity(Intent(context, AdultActivity::class.java))
                activity?.finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkChocolate,
                contentColor = CoinGold
            )
        ) {
            Text(
                text = "Раздел для взрослых",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DepositDialog(
    currentBalance: Int,
    goalTarget: Int,
    currentSaved: Int,
    onDismiss: () -> Unit,
    onConfirm: (amount: Int) -> Unit
) {
    val needed = (goalTarget - currentSaved).coerceAtLeast(0)
    val maxDepositLimit = minOf(500, currentBalance)
    var depositVal by remember { mutableFloatStateOf(0f) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCreamPanel),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Пополнение копилки 🐷",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Баланс: $currentBalance F | Осталось до цели: $needed F",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMediumBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CoinGold)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Отложить: ${depositVal.toInt()} F",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkChocolate
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Слайдер пополнения копилки с верхней границей 500
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Сумма пополнения",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkChocolate
                        )
                        Text(
                            text = "макс. 500 F",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMediumBrown
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = depositVal,
                        onValueChange = {
                            val maxAllowed = maxDepositLimit.toFloat()
                            depositVal = (round(it / 25f) * 25f).coerceIn(0f, maxAllowed)
                        },
                        valueRange = 0f..500f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = CoinGold,
                            activeTrackColor = CoinGold,
                            inactiveTrackColor = CoinGold.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppCreamCapsule,
                            contentColor = TextMediumBrown
                        )
                    ) {
                        Text("Отмена", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onConfirm(depositVal.toInt()) },
                        enabled = depositVal > 0f && currentBalance >= depositVal.toInt(),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BuyButtonGreen,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Text("Отложить", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCoinBadge() {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(CoinGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = 10F
            val radius = (size.minDimension - strokePx) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(
                color = CoinYellowLight,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx)
            )
        }

        Text(
            text = "F",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9)
@Composable
fun ProfileMenuPreview() {
    MainTheme {
        ProfileMenuContent(Data())
    }
}
