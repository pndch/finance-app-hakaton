package furrylovers.finance_app

import android.app.Application
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
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class ShopItem(
    val id: Int,
    val name: String,
    val price: Int,
    val category: ShopCategory,
    val placeholderColor: Color = Color(0xFFFFE0B2)
)

enum class ShopCategory(val title: String) {
    MANDATORY("Обязательные"),
    OPTIONAL("Необязательные"),
    CARE("Уход за персонажем")
}

private val demoShopItems = listOf(
    ShopItem(1, "Яблоко", 50, ShopCategory.MANDATORY, Color(0xFFFFCDD2)),
    ShopItem(2, "Молоко", 80, ShopCategory.MANDATORY, Color(0xFFB3E5FC)),
    ShopItem(3, "Хлеб", 60, ShopCategory.MANDATORY, Color(0xFFFFE0B2)),
    ShopItem(4, "Рыба", 150, ShopCategory.MANDATORY, Color(0xFFB2DFDB)),
    ShopItem(5, "Красная кепка", 200, ShopCategory.OPTIONAL, Color(0xFFFFAB91)),
    ShopItem(6, "Синяя футболка", 250, ShopCategory.OPTIONAL, Color(0xFF90CAF9)),
    ShopItem(7, "Кошачья мята", 120, ShopCategory.OPTIONAL, Color(0xFFC5E1A5)),
    ShopItem(8, "Очки", 180, ShopCategory.OPTIONAL, Color(0xFFB0BEC5)),
    ShopItem(9, "Зубная щётка", 70, ShopCategory.CARE, Color(0xFFB3E5FC)),
    ShopItem(10, "Зубная паста", 90, ShopCategory.CARE, Color(0xFFFFF9C4)),
    ShopItem(11, "Мыло", 40, ShopCategory.CARE, Color(0xFFF8BBD0)),
    ShopItem(12, "Шампунь", 110, ShopCategory.CARE, Color(0xFFD1C4E9))
)

@Composable
fun ShopScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
) {
//    val context = LocalContext.current

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val data by viewModel.data.collectAsStateWithLifecycle()
    var balance by rememberSaveable { mutableIntStateOf(data.money) }

    val categories = ShopCategory.values()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
            .statusBarsPadding()
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
                            viewModel.buyItem(itemId = item.id, price = item.price)

//                            Data().changeMoney(context, -item.price)
//                            Data().changeInventory(context, item.id-1, 1)

                            //Toast.makeText(context, DoJson(context).loadData().inventory[item.id-1].toString(), Toast.LENGTH_SHORT).show()
                        } else {
                            //Toast.makeText(context, "Недостаточно монет!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BalanceBar(balance: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFE0B2))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Магазин",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                CoinBadge()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Баланс: $balance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5D4037)
                )
            }
        }
    }
}

@Composable
private fun CoinBadge() {
    // Деньги
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

@Preview(showBackground = true, widthDp = 380, heightDp = 720)
@Composable
private fun ShopScreenPreview() {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val fakeViewModel = remember {
        GameViewModel(app).apply {
            // ⚠️ только если у тебя есть публичный сеттер или update()
            update { Data(money = 9999) }
        }
    }

    MaterialTheme {
        ShopScreen(viewModel = fakeViewModel)
    }
}
