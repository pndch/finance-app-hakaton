package furrylovers.finance_app.menues

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import furrylovers.finance_app.data_save.Data
import furrylovers.finance_app.data_save.DebugSettings
import furrylovers.finance_app.viewmodel. GameViewModel
import furrylovers.finance_app.quests.QuestCategory
import furrylovers.finance_app.data_save.characters
import furrylovers.finance_app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round

class TitleScreenActivity : ComponentActivity() { // точка входа 2
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setBackgroundDrawableResource(R.color.transparent)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        setContent {
            MainTheme() {
                MainMenu(viewModel)
            }
        }
    }
}

enum class AppDestinations(
    val icon: Int,
) {
    SHOP(furrylovers.finance_app.R.drawable.ic_favorite),
    QUESTS(furrylovers.finance_app.R.drawable.ic_favorite),
    HOME(furrylovers.finance_app.R.drawable.ic_home),
    PROFILE(furrylovers.finance_app.R.drawable.ic_account_box),
}

@Composable
fun MainMenu(viewModel: GameViewModel) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    val destinations = remember { AppDestinations.entries.toTypedArray() }
    val initialPageIndex = remember { destinations.indexOf(AppDestinations.HOME).coerceAtLeast(0) }

    val pagerState = rememberPagerState(
        initialPage = initialPageIndex,
        pageCount = { destinations.size }
    )
    val coroutineScope = rememberCoroutineScope()

    val currentDestination = destinations[pagerState.currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RootBackgroundDark) // фон корня, склеит контент и панель
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            beyondViewportPageCount = 4,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            key = { destinations[it] }
        ) { page ->
            when (destinations[page]) {
                AppDestinations.SHOP -> ShopScreen(viewModel)
                AppDestinations.QUESTS -> QuestMenu(viewModel)
                AppDestinations.HOME -> MainMenuContent(
                    data = data,
                    onFinishDayClick = { food, care, mood ->
                        viewModel.finishDay(food, care, mood)
                    },
                    onLevel3Answer = { correct ->
                        viewModel.completeLevel3Event(correct)
                    },
                    onLevel5Answer = { correct ->
                        viewModel.completeLevel5Event(correct)
                    }
                )
                AppDestinations.PROFILE -> ProfileMenu(viewModel)
            }
        }

        // Панель снизу — своя, не накрывает контент
        BottomPanel(
            currentDestination = currentDestination,
            hasUnreadQuests = data.hasUnreadCompletedQuests,
            onDestinationChange = { targetDest ->
                val targetIndex = destinations.indexOf(targetDest)
                if (targetIndex >= 0) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(targetIndex)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BottomPanel(
    currentDestination: AppDestinations,
    hasUnreadQuests: Boolean = false,
    onDestinationChange: (AppDestinations) -> Unit,
    modifier: Modifier = Modifier
) {
    // фон панели
    val panelColor = AppCreamPanel.copy(alpha = 0.92f)

    val contentInactive = TextMutedBrown.copy(alpha = 0.55f)
    val contentActive = DarkChocolate
    val activeBg = DarkChocolate.copy(alpha = 0.08f)

    Column(
        modifier = modifier
            .background(panelColor)
            .drawBehind {
                drawLine(
                    color = DarkChocolate.copy(alpha = 0.08f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestinations.entries.forEach { destination ->
                val selected = destination == currentDestination
                BottomPanelButton(
                    destination = destination,
                    selected = selected,
                    hasUnreadBadge = hasUnreadQuests,
                    onClick = { onDestinationChange(destination) },
                    activeBg = activeBg,
                    contentActive = contentActive,
                    contentInactive = contentInactive,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomPanelButton(
    destination: AppDestinations,
    selected: Boolean,
    hasUnreadBadge: Boolean = false,
    onClick: () -> Unit,
    activeBg: Color,
    contentActive: Color,
    contentInactive: Color,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) activeBg else Color.Transparent,
        label = "bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) contentActive else contentInactive,
        label = "content"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                painter = painterResource(destination.icon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            if (destination == AppDestinations.QUESTS && hasUnreadBadge) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = when (destination) {
                AppDestinations.SHOP -> "Магазин"
                AppDestinations.QUESTS -> "Квесты"
                AppDestinations.HOME -> "Главная"
                AppDestinations.PROFILE -> "Профиль"
            },
            fontSize = 11.sp,
            color = contentColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
private fun MainMenuContent(
    data: Data,
    onFinishDayClick: (food: Int, care: Int, mood: Int) -> Unit = { _, _, _ -> },
    onLevel3Answer: (Boolean) -> Unit = {},
    onLevel5Answer: (Boolean) -> Unit = {}
) {
    var showFinishDayDialog by remember { mutableStateOf(data.lastFinishDayTime == 0L) }

    val totalEarnedXp = data.quests
        .filter { it.questStatus == QuestCategory.COMPLETED }
        .sumOf { it.questAward } +
        (if (data.level3EventCompleted) 300 else 0) +
        (if (data.level5EventCompleted) 500 else 0)

    val currentLevel = data.petStage + (totalEarnedXp / 300)

    val showLevel3Event = currentLevel >= 3 && !data.level3EventShown
    val showLevel5Event = currentLevel >= 5 && !data.level5EventShown

    if (showLevel3Event) {
        LevelEventDialog(
            level = 3,
            onDismiss = {},
            onAnswer = { correct -> onLevel3Answer(correct) }
        )
    } else if (showLevel5Event) {
        LevelEventDialog(
            level = 5,
            onDismiss = {},
            onAnswer = { correct -> onLevel5Answer(correct) }
        )
    }

    val intervalMs = DebugSettings.finishDayIntervalMs
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val isFinishDayAvailable = remember(data.lastFinishDayTime, now) {
        data.lastFinishDayTime == 0L || (now - data.lastFinishDayTime >= intervalMs)
    }

    if (showFinishDayDialog) {
        FinishDayDialog(
            data = data,
            onDismiss = { showFinishDayDialog = false },
            onConfirm = { food, care, mood ->
                onFinishDayClick(food, care, mood)
                showFinishDayDialog = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(furrylovers.finance_app.R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Деньги
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 16.dp)
                .background(AppCreamPanel.copy(alpha = 0.92f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(CoinGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokePx = 15F
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = data.money.toString(),
                color = DarkChocolate,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Вычисление критических статов (< 5%)
        val foodLow = data.petNeeds.getOrElse(0) { 50 } < 5
        val careLow = data.petNeeds.getOrElse(1) { 50 } < 5
        val moodLow = data.petNeeds.getOrElse(2) { 50 } < 5

        val thoughtMessages = remember(foodLow, careLow, moodLow) {
            mutableListOf<String>().apply {
                if (foodLow) add("Хочу кушать! 🍎")
                if (careLow) add("Мне нужен уход! 🧼")
                if (moodLow) add("Мне очень грустно... 🥺")
            }
        }

        // Расчет уровня и динамического роста питомца
        val totalEarnedXp = data.quests
            .filter { it.questStatus == QuestCategory.COMPLETED }
            .sumOf { it.questAward }
        val currentLevel = data.petStage + (totalEarnedXp / 300)

        val petScale = when {
            currentLevel <= 2 -> 0.90f
            currentLevel in 3..4 -> 1.20f
            else -> 1.50f
        }

        val animatedPetScale by animateFloatAsState(
            targetValue = petScale * 1.8f,
            animationSpec = tween(durationMillis = 800),
            label = "petGrowthScale"
        )

        CharacterLayer(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            characterType = data.petType,
            scale = petScale
        )

        // Динамический отступ для облачка в зависимости от роста питомца
        val bubbleBottomPadding = (160.dp * animatedPetScale) + 160.dp

        // Облачко с мыслями над персонажем
        if (thoughtMessages.isNotEmpty()) {
            CharacterThoughtBubble(
                messages = thoughtMessages,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = bubbleBottomPadding)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Parameters.entries.forEachIndexed { index, parameters ->
                LiquidCircleProgress(
                    index = index,
                    parameters = parameters,
                    progress = data.petNeeds[index] / 100F,
                    modifier = Modifier
                        .size(110.dp)
                        .padding(top = 30.dp, start = 20.dp)
                )
            }
        }

        // Круглая кнопка "Завершить день" под персонажем
        if (isFinishDayAvailable) {
            FinishDayButton(
                onClick = { showFinishDayDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 85.dp)
            )
        }

        ExperienceBar(
            data = data,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun BudgetComparisonCard(
    budget: List<Int>,
    expenses: List<Int>
) {
    val plannedFood = budget.getOrElse(0) { 0 }
    val actualFood = expenses.getOrElse(0) { 0 }

    val plannedCare = budget.getOrElse(1) { 0 }
    val actualCare = expenses.getOrElse(1) { 0 }

    val plannedMood = budget.getOrElse(2) { 0 }
    val actualMood = expenses.getOrElse(2) { 0 }

    val totalPlan = plannedFood + plannedCare + plannedMood
    val hasPlan = totalPlan > 0

    if (!hasPlan) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppCreamCapsule),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📊 Первый день: План ещё не составлялся",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CategoryComparisonRow(
                label = "Еда",
                planned = plannedFood,
                actual = actualFood,
                accentColor = StatFoodOrange
            )

            CategoryComparisonRow(
                label = "Уход",
                planned = plannedCare,
                actual = actualCare,
                accentColor = StatCareTeal
            )

            CategoryComparisonRow(
                label = "Настроение",
                planned = plannedMood,
                actual = actualMood,
                accentColor = StatHappinessPink
            )
        }
    }
}

@Composable
private fun CategoryComparisonRow(
    label: String,
    planned: Int,
    actual: Int,
    accentColor: Color
) {
    val diff = actual - planned
    val isOver = diff > 0

    val statusBg = if (isOver) NotificationErrorBg else NotificationSuccessBg
    val statusText = if (isOver) NotificationErrorText else NotificationSuccessText
    val statusSymbol = if (isOver) "⚠️ +$diff F" else if (diff < 0) "✅ -${-diff} F" else "✅ 0 F"

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppCreamCapsule),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$actual/$planned F",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkChocolate,
                    maxLines = 1
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusSymbol,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusText,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun FinishDayDialog(
    data: Data,
    onDismiss: () -> Unit,
    onConfirm: (food: Int, care: Int, mood: Int) -> Unit
) {
    val currentMoney = data.money
    val maxAvailableCoins = currentMoney + 100
    var foodVal by remember { mutableFloatStateOf(0f) }
    var careVal by remember { mutableFloatStateOf(0f) }
    var moodVal by remember { mutableFloatStateOf(0f) }

    val totalAllocated = (foodVal + careVal + moodVal).toInt()
    val isLimitExceeded = totalAllocated > maxAvailableCoins
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCreamPanel),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Завершение дня ✨",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Сравнение факта и плана по каждой категории
                BudgetComparisonCard(
                    budget = data.budget,
                    expenses = data.expenses
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Доступно: $maxAvailableCoins монет (Баланс $currentMoney + 100)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMediumBrown
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isLimitExceeded) NotificationErrorBg else NotificationSuccessBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Распределено: $totalAllocated / $maxAvailableCoins монет",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLimitExceeded) NotificationErrorText else NotificationSuccessText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Слайдер 1: Еда
                SliderRow(
                    label = "Еда",
                    value = foodVal,
                    onValueChange = { foodVal = (round(it / 25f) * 25f) },
                    activeColor = StatFoodOrange
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Слайдер 2: Настроение
                SliderRow(
                    label = "Настроение",
                    value = moodVal,
                    onValueChange = { moodVal = (round(it / 25f) * 25f) },
                    activeColor = StatHappinessPink
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Слайдер 3: Уход
                SliderRow(
                    label = "Уход",
                    value = careVal,
                    onValueChange = { careVal = (round(it / 25f) * 25f) },
                    activeColor = StatCareTeal
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Кнопки действия
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
                        onClick = {
                            onConfirm(foodVal.toInt(), careVal.toInt(), moodVal.toInt())
                        },
                        enabled = !isLimitExceeded,
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
                        Text("Подтвердить", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkChocolate
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(activeColor)
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${value.toInt()} монет",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkChocolate
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..200f,
            steps = 7,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = activeColor.copy(alpha = 0.25f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FinishDayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkChocolate,
            contentColor = CoinGold
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "✨",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Завершить\nдень",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CoinGold,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
        }
    }
}

@Composable
private fun ExperienceBar(
    data: Data,
    modifier: Modifier = Modifier
) {
    val totalEarnedXp = data.quests
        .filter { it.questStatus == QuestCategory.COMPLETED }
        .sumOf { it.questAward } +
        (if (data.level3EventCompleted) 300 else 0) +
        (if (data.level5EventCompleted) 500 else 0)

    val levelXpThreshold = 300
    val level = data.petStage + (totalEarnedXp / levelXpThreshold)
    val currentXp = totalEarnedXp % levelXpThreshold
    val maxXp = levelXpThreshold
    val progressFraction = (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "xpProgress"
    )

    val barBgColor = AppCreamPanel.copy(alpha = 0.95f)

    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Имя питомца над полоской опыта
        Box(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DarkChocolate.copy(alpha = 0.88f))
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.petName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CoinGold
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(barBgColor)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Значок уровня
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkChocolate)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ур. $level",
                    color = CoinGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Шкала прогресса опыта
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(AppTrackBg),
                contentAlignment = Alignment.CenterStart
            ) {
                // Зеленый индикатор
                if (animatedProgress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        DarkGreen,
                                        SuccessGreenBright,
                                        LightGreen
                                    )
                                )
                            )
                    ) {
                        // Блик сверху
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color.White.copy(alpha = 0.35f))
                        )
                    }
                }

                // Текст опыта по центру
                Text(
                    text = "$currentXp / $maxXp XP",
                    modifier = Modifier.align(Alignment.Center),
                    color = NotificationSuccessText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun CharacterThoughtBubble(
    messages: List<String>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleFloat")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Column(
        modifier = modifier.graphicsLayer { translationY = offsetY },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                messages.forEach { msg ->
                    Text(
                        text = msg,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkBrown,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Хвостик облачка с мыслями
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
        )
    }
}

@Composable
fun LiquidCircleProgress(
    parameters: Parameters,
    index: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = parameters.color,
    strokeColor: Color = parameters.strokeColor,
    strokeWidth: Dp = 6.dp,
    animate: Boolean = false
) {
    val isCritical = progress < 0.05f

    val pulseTransition = rememberInfiniteTransition(label = "pulse_$index")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val activeStrokeColor = if (isCritical) Color(0xFFE53935).copy(alpha = pulseAlpha) else strokeColor
    val activeStrokeWidth = if (isCritical) 8.dp else strokeWidth

    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = if (animate) 600 else 0),
        label = "liquidProgress"
    )

    val transition = rememberInfiniteTransition(label = "bubble_$index")
    val durationMillis = 2200 + index * 400
    val offsetY by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_$index"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer { translationY = offsetY },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = activeStrokeWidth.toPx()
            val radius = (size.minDimension - strokePx) / 2.3f
            val center = Offset(size.width / 2f, size.height / 2f)

            clipPath(Path().apply { addOval(Rect(center, radius)) }) {
                val fillHeight = radius * 2f * animated
                val topLeftY = center.y + radius - fillHeight
                drawRect(
                    color = fillColor,
                    topLeft = Offset(center.x - radius, topLeftY),
                    size = Size(radius * 2f, fillHeight)
                )
            }

            drawCircle(
                color = activeStrokeColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx)
            )
        }

        Icon(
            painter = painterResource(parameters.icon),
            contentDescription = parameters.label,
            modifier = Modifier.size(28.dp)
        )

        if (isCritical) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun CharacterLayer(
    modifier: Modifier = Modifier,
    characterType: Int,
    scale: Float = 1.0f
) {
    val animatedScale by animateFloatAsState(
        targetValue = scale * 1.8f,
        animationSpec = tween(durationMillis = 800),
        label = "characterScale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(characters[characterType.coerceIn(0, characters.size - 1)]),
            contentDescription = null,
            modifier = Modifier
                .size(500.dp)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                },
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun LevelEventDialog(
    level: Int,
    onDismiss: () -> Unit,
    onAnswer: (correct: Boolean) -> Unit
) {
    val isLevel3 = level == 3
    val title = if (isLevel3) "🎉 Событие 3 уровня: Обед для питомца!" else "🏆 Событие 5 уровня: Домик мечты!"
    val question = if (isLevel3) {
        "Твой питомец просит премиальный корм за 120 монет, но в кошельке только 80 монет. В копилке отложено 100 монет. Сколько монет нужно взять из копилки, чтобы купить корм и сколько останется в копилке?"
    } else {
        "Загородный дом для питомца стоит 500 монет. Ты накопил 340 монет, а родители добавили в подарок ещё 50% от твоих накоплений. Хватит ли вам денег?"
    }

    val options = if (isLevel3) {
        listOf(
            "Взять 40 монет, останется 60 F" to true,
            "Взять 50 монет, останется 50 F" to false,
            "Взять 80 монет, останется 20 F" to false
        )
    } else {
        listOf(
            "Всего 510 монет — хватит на домик!" to true,
            "Всего 450 монет — не хватит" to false,
            "Всего 390 монет — не хватит" to false
        )
    }

    var isCorrect by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }

    val explanationText = if (isLevel3) {
        "💡 Объяснение: 120 (цена корма) - 80 (в кошельке) = 40 монет нужно взять из копилки. 100 - 40 = 60 монет останется в копилке. Правильный ответ: взять 40, останется 60 F."
    } else {
        "💡 Объяснение: 50% от 340 = 170 монет. 340 + 170 = 510 монет. Так как 510 больше 500, денег хватает на покупку дома!"
    }

    Dialog(onDismissRequest = {}) {
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
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = question,
                    fontSize = 13.sp,
                    color = TextMediumBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!showExplanation) {
                    options.forEach { (optionText, correct) ->
                        Button(
                            onClick = {
                                isCorrect = correct
                                showExplanation = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppCreamCapsule,
                                contentColor = DarkChocolate
                            )
                        ) {
                            Text(text = optionText, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isCorrect) NotificationSuccessBg else NotificationErrorBg)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isCorrect) "🎉 Правильно! +${if (isLevel3) 300 else 500} XP" else "❌ Неверно!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) NotificationSuccessText else NotificationErrorText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = explanationText,
                                fontSize = 12.sp,
                                color = if (isCorrect) NotificationSuccessText else NotificationErrorText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onAnswer(isCorrect)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BuyButtonGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Продолжить", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

enum class Parameters(
    val label: String,
    val icon: Int,
    val color: Color,
    val strokeColor: Color
) {
    FULLNESS(
        "Сытость",
        furrylovers.finance_app.R.drawable.ic_favorite,
        color = StatFoodOrange,
        strokeColor = StatFoodStroke
    ),
    GROOMED(
        "Уход",
        furrylovers.finance_app.R.drawable.ic_favorite,
        color = StatCareTeal,
        strokeColor = StatCareStroke
    ),
    HAPPINESS(
        "Счастье",
        furrylovers.finance_app.R.drawable.ic_favorite,
        color = StatHappinessPink,
        strokeColor = StatHappinessStroke
    ),
}

@Preview(showBackground = true, device = Devices.PIXEL_9)
@Composable
fun MainMenuPreview() {
    MainTheme() {
        MainMenuContent(Data())
    }
}
