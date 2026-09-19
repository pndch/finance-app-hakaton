package furrylovers.finance_app

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import furrylovers.finance_app.ui.theme.MainTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

class TitleScreenActivity : ComponentActivity() { //точка входа 2
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setBackgroundDrawableResource(android.R.color.transparent)

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


@Composable
fun MainMenu(viewModel: GameViewModel) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1 //сколько держит панелек в памяти
        ) { page ->
            // 0.0 - в фокусе, 1.0 - ушла влево, -1.0 - пришла справа
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (pageOffset > 0) {
                            // уход под другую
                            val scale = 0.85f + (1f - 0.85f) * (1f - pageOffset.coerceIn(0f, 1f))
                            scaleX = scale
                            scaleY = scale
                            alpha = 1f - pageOffset.coerceIn(0f, 1f)
                            translationX = pageOffset * size.width * 0.5f
                        } else {
                            // накладка сверъу
                            translationX = 0f
                            scaleX = 1f
                            scaleY = 1f
                            alpha = 1f
                        }
                    }
                    .zIndex(if (pageOffset > 0) 0f else 1f)
                    .clip(RoundedCornerShape(if (abs(pageOffset) > 0.001f) 24.dp else 0.dp))
            ) {
                if (page == 0) {
                    HomeContent(viewModel)
                }
                if (page == 1){
                    ShopScreen(viewModel)
                }
                if (page==2) {
                    AdultMenu()
                }

                //надо норм сделать
            }
        }
    }
}

@Composable
private fun HomeContent(viewModel: GameViewModel) {
//    val context = LocalContext.current
    val data by viewModel.data.collectAsStateWithLifecycle()
//    val data = remember { try { DoJson(context).loadData() } catch (e: Exception) { Data() } }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.background),
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
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(Color(0xFFFFC107), CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokePx = 15F
                    val radius = (size.minDimension - strokePx) / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)


                    drawCircle(
                        color = Color(0xFFFFEB3B),
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
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

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
                    progress = data.petNeeds[index]/100F,
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


enum class Parameters( //кнопки внизу экрана
    val label: String,
    val icon: Int,
    val color: Color,
    val strokeColor: Color
) {
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
    HAPPINESS("Счастье",
        R.drawable.ic_favorite,
        color = Color(0xFFFF6B9D),
         strokeColor = Color(0xFFD6336C)
    ),
}

