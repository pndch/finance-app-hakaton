package furrylovers.finance_app

object QuestManager {
    fun processEvent(
        quests: List<Quests>,
        type: QuestType,
        amount: Int,
        onQuestCompleted: (quest: Quests, rewardCoins: Int) -> Unit = { _, _ -> }
    ): MutableList<Quests> {
        return quests.map { quest ->
            if (quest.questStatus == QuestCategory.UNCOMPLETED && quest.questType == type) {
                val newProgress = (quest.questProgress + amount).coerceAtMost(quest.targetValue)
                val isCompleted = newProgress >= quest.targetValue

                if (isCompleted && quest.questStatus == QuestCategory.UNCOMPLETED) {
                    onQuestCompleted(quest, quest.questAward)
                }

                quest.copy(
                    questProgress = newProgress,
                    questStatus = if (isCompleted) QuestCategory.COMPLETED else QuestCategory.UNCOMPLETED
                )
            } else {
                quest
            }
        }.toMutableList()
    }
}
