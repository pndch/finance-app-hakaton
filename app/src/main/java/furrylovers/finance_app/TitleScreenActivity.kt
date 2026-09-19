package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import furrylovers.finance_app.ui.theme.MainTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class TitleScreenActivity : ComponentActivity() { //точка входа 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setBackgroundDrawableResource(android.R.color.transparent)
        setContent {
            MainTheme() {
                MainMenu()
            }
        }
    }
}

//@PreviewScreenSizes
@Composable
fun MainMenu() {
    // Текущий экран приложения: HOME, FAVORITES, SHOP, MINIGAMES
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Общий фон для всех экранов ──
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ── Переключение основного контента в зависимости от выбранной вкладки ──
        when (currentDestination) {
            AppDestinations.SHOP -> {
                // Экран магазина: своя вкладка, свой скролл, свой баланс
                ShopScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding() // чтобы верхняя плашка не уезжала под статус-бар
                )
            }
            AppDestinations.DEBUG -> {
                AdultMenu()
            }

            else -> {
                // HOME и пока-что-заглушки для Бюджета / Мини-игр
                HomeContent()
            }
        }

        // ── Навигация: в магазине — снизу горизонтально, в остальных экранах — справа сверху ──
        if (currentDestination == AppDestinations.SHOP) {

            // Нижняя горизонтальная панель навигации (только для магазина)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppDestinations.entries.forEachIndexed { index, destination ->
                    BubbleButton(
                        destination = destination,
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination },
                        index = index
                    )
                }
            }
        } else {

            // Верхнее вертикальное меню навигации (для всех остальных экранов)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppDestinations.entries.forEachIndexed { index, destination ->
                    BubbleButton(
                        destination = destination,
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination },
                        index = index
                    )
                }
            }
        }
    }
}

/**
 * Домашний экран: персонаж по центру + три кружка-стата снизу.
 * Всё, что раньше было внутри MainMenu(), кроме навигации и фона.
 */
@Composable
private fun HomeContent() {
    var liquidProgress by rememberSaveable { mutableStateOf(0.4f) }

    Box(modifier = Modifier.fillMaxSize()) {

        CharacterLayer(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(bottom = 30.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Parameters.entries.forEachIndexed { index, parameters ->
                LiquidCircleProgress(
                    index = index,
                    parameters = parameters,
                    progress = liquidProgress,
                    modifier = Modifier
                        .size(110.dp)
                        .padding(top = 30.dp, start = 20.dp)
                )
            }
        }
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
    animate: Boolean = true
) {
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
            val strokePx = strokeWidth.toPx()
            val radius = (size.minDimension - strokePx) / 2f
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
                color = strokeColor,
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
    }
}

@Composable
fun CharacterLayer(modifier: Modifier = Modifier) {
    // ── МЕСТО 1: базовое «дыхание» (постоянная лёгкая анимация) ──
    val idleTransition = rememberInfiniteTransition(label = "char_idle")
    // val idleScale by idleTransition.animateFloat(...)

    // ── МЕСТО 2: триггер случайной эмоции/действия раз в 8–15 сек ──
    var actionTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            val delayMs = Random.nextLong(8_000L, 15_000L)
            delay(delayMs)

            // ── МЕСТО 2а: тут выбираем, какое действие проиграть ──
            actionTrigger++
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.character), // ← твоя картинка персонажа
            contentDescription = null,
            modifier = Modifier
                .size(620.dp),

            // ── МЕСТО 1а: сюда вешаем idle-трансформации ──
            // ── МЕСТО 2б: сюда вешаем трансформации по actionTrigger ──
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun BubbleButton(
    destination: AppDestinations,
    selected: Boolean,
    onClick: () -> Unit,
    index: Int
) {
    val transition = rememberInfiniteTransition(label = "bubble_$index")

    // разные фазы плавания за счёт разных длительностей
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .graphicsLayer { translationY = offsetY }
                .clip(CircleShape)
                .background(
                    if (selected) Color.White.copy(alpha = 0.35f)
                    else Color.White.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(destination.icon),
                contentDescription = destination.label,
                tint = if (selected) Color.Black else Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = destination.label,
            color = if (selected) Color.Black else Color.Black.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

enum class Parameters( //кнопки внизу экрана
    val label: String,
    val icon: Int,
    val color: Color,
    val strokeColor: Color
) {
    HAPPINESS("Счастье",
        R.drawable.ic_favorite,
        color = Color(0xFFFF6B9D),
         strokeColor = Color(0xFFD6336C)
    ),
    FULLNESS("Сытость",
        R.drawable.ic_favorite,
        color = Color(0xFFFFA94D),
         strokeColor = Color(0xFFD97A1F)
    ),
    GROOMED("Уход",
        R.drawable.ic_favorite,
        color = Color(0xFF4ECDC4),
         strokeColor = Color(0xFF2E9E96)
    ),

}

enum class AppDestinations( //кнопки навигации в боковом меню
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    FAVORITES("Бюджет", R.drawable.ic_favorite),
    SHOP("Shop", R.drawable.ic_favorite),
    MINIGAMES("MiniGames", R.drawable.ic_favorite),
    DEBUG("Debug", R.drawable.ic_favorite),
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    MainMenu()
}
