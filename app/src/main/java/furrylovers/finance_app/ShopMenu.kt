package furrylovers.finance_app

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import furrylovers.finance_app.ui.theme.*
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

    var boughtItemForStats by remember { mutableStateOf<ShopItem?>(null) }

    val categories = ShopCategory.entries.toTypedArray()

    LaunchedEffect(notificationMessage) {
        if (notificationMessage != null) {
            delay(2500L)
            notificationMessage = null
        }
    }

    LaunchedEffect(boughtItemForStats) {
        if (boughtItemForStats != null) {
            delay(3000L)
            boughtItemForStats = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackgroundWarm)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.End
        ) {
            BalanceBar(balance = balance)

            CategorySelector(
                categories = categories,
                selectedIndex = selectedTab,
                onCategorySelected = { selectedTab = it },
                modifier = Modifier.fillMaxWidth()
            )

            val itemsForCategory = demoShopItems.filter { it.category == categories[selectedTab] }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                                boughtItemForStats = item
                            } else {
                                notificationMessage = "Недостаточно монет для покупки!"
                                isSuccessNotification = false
                            }
                        }
                    )
                }
            }
        }

        // Всплывающее уведомление сверху (о статусе покупки)
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
                        containerColor = if (isSuccessNotification) NotificationSuccessBg else NotificationErrorBg
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
                                .background(if (isSuccessNotification) SuccessGreenBright else NotificationErrorBadge),
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
                            color = if (isSuccessNotification) NotificationSuccessText else NotificationErrorText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Всплывающее окно снизу (изменения характеристик)
        AnimatedVisibility(
            visible = boughtItemForStats != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            boughtItemForStats?.let { item ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkChocolate),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Изменение характеристик питомца:",
                            color = CoinGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.food > 0) {
                                StatChip(
                                    label = "+${item.food} Сытость",
                                    bgColor = StatFoodOrange,
                                    textColor = DarkChocolate
                                )
                            }
                            if (item.care > 0) {
                                StatChip(
                                    label = "+${item.care} Уход",
                                    bgColor = StatCareTeal,
                                    textColor = DarkChocolate
                                )
                            }
                            if (item.happiness > 0) {
                                StatChip(
                                    label = "+${item.happiness} Счастье",
                                    bgColor = StatHappinessPink,
                                    textColor = DarkChocolate
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun CategorySelector(
    categories: Array<ShopCategory>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(AppCreamCapsule)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEachIndexed { index, category ->
                val selected = index == selectedIndex
                val chipBgColor by animateColorAsState(
                    targetValue = if (selected) DarkChocolate else Color.Transparent,
                    animationSpec = tween(300),
                    label = "categoryBg"
                )
                val chipTextColor by animateColorAsState(
                    targetValue = if (selected) CoinGold else TextMediumBrown,
                    animationSpec = tween(300),
                    label = "categoryText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(chipBgColor)
                        .clickable { onCategorySelected(index) }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.title,
                        color = chipTextColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
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
            text = balance.toString(),
            color = DarkChocolate,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CoinBadgeSmall() {
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(CoinGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = 8F
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
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ShopItemCard(
    item: ShopItem,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Картинка товара в центре
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(item.placeholderColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = TextMediumBrown,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Описание (название)
            Row() {
                Text(
                    text = item.name + " ${item.price}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkBrown,
                    maxLines = 1
                )

                CoinBadgeSmall()
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Кнопка покупки
            Button(
                onClick = onBuyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BuyButtonGreen,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Купить",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
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
