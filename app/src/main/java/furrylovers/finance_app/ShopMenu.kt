package furrylovers.finance_app

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import furrylovers.finance_app.ui.theme.MainTheme
import kotlinx.coroutines.delay
import java.io.File

data class ShopItem(
    val id: Int,
    val name: String,
    val price: Int,
    val category: ShopCategory,
    val placeholderColor: Color = Color(0xFFFFE0B2),
    val food: Int,
    val care: Int,
    val happiness: Int
)

enum class ShopCategory(val title: String) {
    MANDATORY("Обязательные"),
    OPTIONAL("Необязательные")
}

private val demoShopItems = listOf(
    ShopItem(1, "Яблоко", 50, ShopCategory.MANDATORY, Color(0xFFFFCDD2), food = 15, care = 0, happiness = 5),
    ShopItem(2, "Молоко", 80, ShopCategory.MANDATORY, Color(0xFFB3E5FC), food = 20, care = 0, happiness = 5),
    ShopItem(3, "Хлеб", 60, ShopCategory.MANDATORY, Color(0xFFFFE0B2), food = 25, care = 0, happiness = 3),
    ShopItem(4, "Рыба", 150, ShopCategory.MANDATORY, Color(0xFFB2DFDB), food = 35, care = 0, happiness = 8),
    ShopItem(5, "Зубная щётка", 70, ShopCategory.MANDATORY, Color(0xFFB3E5FC), food = 0, care = 20, happiness = 5),
    ShopItem(6, "Зубная паста", 90, ShopCategory.MANDATORY, Color(0xFFFFF9C4), food = 0, care = 25, happiness = 5),
    ShopItem(7, "Мыло", 40, ShopCategory.MANDATORY, Color(0xFFF8BBD0), food = 0, care = 15, happiness = 3),
    ShopItem(8, "Шампунь", 110, ShopCategory.MANDATORY, Color(0xFFD1C4E9), food = 0, care = 30, happiness = 8)
)

@Composable
fun ShopScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val data by viewModel.data.collectAsStateWithLifecycle()
    var balance by rememberSaveable { mutableIntStateOf(data.money) }

    var notificationMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessNotification by remember { mutableStateOf(true) }

    val categories = ShopCategory.values()

    LaunchedEffect(notificationMessage) {
        if (notificationMessage != null) {
            delay(2500L)
            notificationMessage = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF8E1))
                .statusBarsPadding(),
            horizontalAlignment = Alignment.End
        ) {
            BalanceBar(balance = balance)

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = Color(0xFFFFF3E0),
                contentColor = Color(0xFFEF6C00),
                divider = {}
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = category.title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    )
                }
            }

            val itemsForCategory = demoShopItems.filter { it.category == categories[selectedTab] }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(itemsForCategory, key = { it.id }) { item ->
                    ShopItemCard(
                        item = item,
                        onBuyClick = {
                            if (balance >= item.price) {
                                balance -= item.price
                                viewModel.buyItem(
                                    itemId = item.id,
                                    price = item.price,
                                    stats = mutableListOf(item.food, item.care, item.happiness)
                                )
                                notificationMessage = "Покупка совершена успешно!"
                                isSuccessNotification = true
                            } else {
                                notificationMessage = "Недостаточно монет для покупки!"
                                isSuccessNotification = false
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        AnimatedVisibility(
            visible = notificationMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(start = 20.dp, top = 12.dp, end = 20.dp)
        ) {
            notificationMessage?.let { msg ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccessNotification) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSuccessNotification) Color(0xFF4CAF50) else Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isSuccessNotification) "✓" else "!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Text(
                            text = msg,
                            color = if (isSuccessNotification) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceBar(
    balance: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 16.dp, end = 16.dp)
            .background(Color(0xFFFFF6E0).copy(alpha = 0.92f), RoundedCornerShape(20.dp))
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
            text = balance.toString(),
            color = Color(0xFF2E2408),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CoinBadge() {
    Row(
        modifier = Modifier
            .statusBarsPadding()
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
    }
}

@Composable
private fun ShopItemCard(
    item: ShopItem,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(item.placeholderColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = Color(0xFF6D4C41),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinBadge()
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.price} монет",
                        fontSize = 14.sp,
                        color = Color(0xFF6D4C41)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onBuyClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF66BB6A),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Купить",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9)
@Composable
fun ShopScreenPreview() {
    val fakeApp = remember {
        object : Application() {
            override fun getFilesDir(): File {
                return File(System.getProperty("java.io.tmpdir") ?: ".")
            }
        }
    }
    MainTheme() {
        ShopScreen(viewModel = GameViewModel(fakeApp))
    }
}
