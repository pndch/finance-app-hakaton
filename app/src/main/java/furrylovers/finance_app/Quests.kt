package furrylovers.finance_app

import kotlinx.serialization.Serializable

@Serializable
data class Quests(
    val id: Int = 0,
    val questName: String = "",
    val questDescription: String = "",
    val questAward: Int = 0,

    var questProgress: Int = 0, // 0-100 или как хотите ваще
    var questStatus: QuestCategory = QuestCategory.UNCOMPLETED
) { }

@Serializable
data class QuestsList(
    var quests: MutableList<Quests> = emptyList<Quests>().toMutableList()
) { }

enum class QuestCategory(val title: String) {
    UNCOMPLETED("Незавершенные"),
    COMPLETED("Завершенные"),
}

//tmp
val questsItems: MutableList<Quests> = mutableListOf(
        Quests(id = 0, questName = "1", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 1, questName = "2", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 2, questName = "3", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 3, questName = "4", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 4, questName = "5", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 5, questName = "6", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 6, questName = "7", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 7, questName = "8", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 8, questName = "9", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
        Quests(id = 9, questName = "10", questDescription = "desription", questAward = 100, questProgress = 0, questStatus = QuestCategory.UNCOMPLETED),
    )