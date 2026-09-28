package furrylovers.finance_app.menues

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import furrylovers.finance_app.data_save.Data
import furrylovers.finance_app.viewmodel.GameViewModel
import furrylovers.finance_app.quests.QuestCategory
import furrylovers.finance_app.quests.QuestType
import furrylovers.finance_app.quests.Quests
import furrylovers.finance_app.R
import furrylovers.finance_app.ui.theme.*

@Composable
fun QuestMenu(viewModel: GameViewModel) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    QuestMenuContent(
        data = data,
        onClaimClick = { questId ->
            viewModel.claimQuestReward(questId)
        },
        onAnswerSelected = { questId, answer ->
            viewModel.solvePuzzleQuest(questId, answer)
        },
        onTabSelected = { categoryIndex ->
            if (categoryIndex == 1) {
                viewModel.markCompletedQuestsAsViewed()
            }
        }
    )
}

@Composable
private fun CategorySelector(
    categories: Array<QuestCategory>,
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
fun QuestMenuContent(
    data: Data,
    onClaimClick: (Int) -> Unit = {},
    onAnswerSelected: (questId: Int, answer: String) -> Unit = { _, _ -> },
    onTabSelected: (Int) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val questCategories = remember { QuestCategory.entries.toTypedArray() }

    LaunchedEffect(selectedTab) {
        onTabSelected(selectedTab)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundWarm)
    ) {
        Image(
            painter = painterResource(R.drawable.photo_questmenu),
            contentDescription = null,
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        CategorySelector(
            categories = questCategories,
            selectedIndex = selectedTab,
            onCategorySelected = { selectedTab = it },
            modifier = Modifier.fillMaxWidth()
        )

        val itemsForCategory = remember(data.quests, selectedTab) {
            data.quests.filter { it.questStatus == questCategories[selectedTab] }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(itemsForCategory, key = { it.id }) { item ->
                if (selectedTab == 0) {
                    QuestItemCard(
                        item = item,
                        onClaimClick = {
                            onClaimClick(item.id)
                        },
                        onAnswerSelected = { answer ->
                            onAnswerSelected(item.id, answer)
                        }
                    )
                } else {
                    CompletedQuestItemCard(
                        item = item
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun QuestItemCard(
    item: Quests,
    onClaimClick: () -> Unit,
    onAnswerSelected: (String) -> Unit = {}
) {
    val isPuzzle = item.questType == QuestType.PUZZLE
    val progressFraction = (item.questProgress.toFloat() / item.targetValue.toFloat()).coerceIn(0f, 1f)
    val isReadyToClaim = item.questProgress >= item.targetValue

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(600),
        label = "questProgress"
    )

    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isFeedbackError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.questName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkBrown
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.questDescription,
                        fontSize = 13.sp,
                        color = TextMediumBrown
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppCreamPanel)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "⭐", fontSize = 14.sp)
                        Text(
                            text = "+${item.questAward} XP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkChocolate
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isPuzzle && !isReadyToClaim) {
                Text(
                    text = "Выберите правильный ответ:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolate
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.puzzleOptions.take(2).forEach { option ->
                        Button(
                            onClick = {
                                if (option == item.correctAnswer) {
                                    feedbackMessage = "🎉 Правильно! +${item.questAward} XP"
                                    isFeedbackError = false
                                    onAnswerSelected(option)
                                } else {
                                    feedbackMessage = "❌ Неверно! Попробуй ещё раз."
                                    isFeedbackError = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppCreamCapsule,
                                contentColor = DarkChocolate
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text(text = option, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                if (item.puzzleOptions.size > 2) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.puzzleOptions.drop(2).forEach { option ->
                            Button(
                                onClick = {
                                    if (option == item.correctAnswer) {
                                        feedbackMessage = "🎉 Правильно! +${item.questAward} XP"
                                        isFeedbackError = false
                                        onAnswerSelected(option)
                                    } else {
                                        feedbackMessage = "❌ Неверно! Попробуй ещё раз."
                                        isFeedbackError = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppCreamCapsule,
                                    contentColor = DarkChocolate
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(text = option, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                if (feedbackMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isFeedbackError) NotificationErrorBg else NotificationSuccessBg)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feedbackMessage!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFeedbackError) NotificationErrorText else NotificationSuccessText
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Прогресс:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMediumBrown
                    )
                    Text(
                        text = "${item.questProgress} / ${item.targetValue}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkChocolate
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AppTrackBg),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (animatedProgress > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isReadyToClaim) BuyButtonGreen else StatFoodOrange
                                )
                        )
                    }
                }

                if (isReadyToClaim) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onClaimClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BuyButtonGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Забрать награду (+${item.questAward} XP)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedQuestItemCard(
    item: Quests
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NotificationSuccessBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✅", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.questName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkBrown
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.questDescription,
                    fontSize = 13.sp,
                    color = TextMediumBrown
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NotificationSuccessBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+${item.questAward} XP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotificationSuccessText
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9)
@Composable
fun QuestMenuPreview() {
    MainTheme {
        QuestMenuContent(Data())
    }
}
