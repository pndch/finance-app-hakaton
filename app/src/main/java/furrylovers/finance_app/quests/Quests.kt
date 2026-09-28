package furrylovers.finance_app.quests

import kotlinx.serialization.Serializable

@Serializable
enum class QuestType {
    SPEND_ON_FOOD,
    SPEND_ON_CARE,
    SPEND_TOTAL_MONEY,
    DEPOSIT_TO_BANK,
    FINISH_DAYS,
    PUZZLE
}

@Serializable
data class Quests(
    val id: Int = 0,
    val questName: String = "",
    val questDescription: String = "",
    val questAward: Int = 100, // Опыт (XP)
    val questType: QuestType = QuestType.SPEND_ON_FOOD,
    val targetValue: Int = 100,
    val puzzleOptions: List<String> = emptyList(),
    val correctAnswer: String = "",

    var questProgress: Int = 0,
    var questStatus: QuestCategory = QuestCategory.UNCOMPLETED
)

enum class QuestCategory(val title: String) {
    UNCOMPLETED("Незавершенные"),
    COMPLETED("Завершенные"),
}

val questsItems: MutableList<Quests> = mutableListOf(
    Quests(
        id = 0,
        questName = "Вкусный обед",
        questDescription = "Потрать 100 монет на еду",
        questAward = 150,
        questType = QuestType.SPEND_ON_FOOD,
        targetValue = 100,
        questProgress = 0,
        questStatus = QuestCategory.UNCOMPLETED
    ),
    Quests(
        id = 1,
        questName = "Чистота и уход",
        questDescription = "Потрать 100 монет на средства ухода",
        questAward = 150,
        questType = QuestType.SPEND_ON_CARE,
        targetValue = 100,
        questProgress = 0,
        questStatus = QuestCategory.UNCOMPLETED
    ),
    Quests(
        id = 2,
        questName = "Шопоголик",
        questDescription = "Потрать всего 300 монет в магазине",
        questAward = 200,
        questType = QuestType.SPEND_TOTAL_MONEY,
        targetValue = 300,
        questProgress = 0,
        questStatus = QuestCategory.UNCOMPLETED
    ),
    Quests(
        id = 3,
        questName = "Бережливый хозяин",
        questDescription = "Отложи 200 монет в копилку",
        questAward = 250,
        questType = QuestType.DEPOSIT_TO_BANK,
        targetValue = 200,
        questProgress = 0,
        questStatus = QuestCategory.UNCOMPLETED
    ),
    Quests(
        id = 4,
        questName = "Новый день",
        questDescription = "Заверши 2 дня",
        questAward = 300,
        questType = QuestType.FINISH_DAYS,
        targetValue = 2,
        questProgress = 0,
        questStatus = QuestCategory.UNCOMPLETED
    ),
    Quests(
        id = 5,
        questName = "🧮 Задачка: Бюджет и Еда",
        questDescription = "Бюджет 100 руб. 50 руб. потрачено на уход, а на еду — половина от трат на уход. Сколько осталось для копилки?",
        questAward = 200,
        questType = QuestType.PUZZLE,
        targetValue = 1,
        puzzleOptions = listOf("15 руб", "25 руб", "35 руб", "50 руб"),
        correctAnswer = "25 руб",
        questProgress = 0,
        questStatus = QuestCategory.UNCOMPLETED
    ),
    Quests(
        id = 6,
        questName = "🧮 Задачка: Умный процент",
        questDescription = "Твой доход 200 руб. Ты решил отложить 25% в копилку. Сколько монет уйдёт в копилку?",
        questAward = 250,
        questType = QuestType.PUZZLE,
        targetValue = 1,
        puzzleOptions = listOf("25 руб", "40 руб", "50 руб", "75 руб"),
        correctAnswer = "50 руб",
        questProgress = 0,
        questStatus = QuestCategory.UNCOMPLETED
    )
)
