package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import furrylovers.finance_app.ui.theme.MainTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay
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
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        CharacterLayer(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(bottom= 140.dp)
        )


        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 15.dp),
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
    }
}

@Composable
fun CharacterLayer(modifier: Modifier = Modifier) {
    // ── МЕСТО 1: базовое «дыхание» (постоянная лёгкая анимация) ──
    // тут можно завести rememberInfiniteTransition для idle-покачивания,
    // например scale 1f ↔ 1.02f или translationY -2f ↔ 2f
    val idleTransition = rememberInfiniteTransition(label = "char_idle")
    // val idleScale by idleTransition.animateFloat(...)

    // ── МЕСТО 2: триггер случайной эмоции/действия раз в 8–15 сек ──
    // тут заводим переменную, которая переключается по таймеру
    var actionTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            // случайная пауза 8000..15000 мс
            val delayMs = Random.nextLong(8_000L, 15_000L)
            delay(delayMs)

            // ── МЕСТО 2а: тут выбираем, какое действие проиграть ──
            // например: actionTrigger = Random.nextInt(0, 3)
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
                .size(620.dp)

            // ── МЕСТО 1а: сюда вешаем idle-трансформации ──
            // .graphicsLayer { scaleX = idleScale; scaleY = idleScale }
            // ── МЕСТО 2б: сюда вешаем трансформации по actionTrigger ──
            // .graphicsLayer { rotationZ = actionRotation }
            ,
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
                tint = if (selected) Color.White else Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = destination.label,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

enum class AppDestinations( //кнопки внизу экрана
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    FAVORITES("Бюджет", R.drawable.ic_favorite),
    SHOP("Shop", R.drawable.ic_favorite),
    MINIGAMES("MiniGames", R.drawable.ic_favorite),

}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    MainMenu()
}