package furrylovers.finance_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val saver = JsonData(app)

    private val _data = MutableStateFlow(
        try {
            val loaded = saver.loadData()
            if (loaded.quests.isEmpty()) {
                val initialized = loaded.copy(quests = questsItems.toMutableList())
                saver.saveData(initialized)
                initialized
            } else {
                loaded
            }
        } catch (e: Exception) {
            val initial = Data(quests = questsItems.toMutableList())
            saver.saveData(initial)
            initial
        }
    )
    val data: StateFlow<Data> = _data.asStateFlow()

    fun update(transform: (Data) -> Data) {
        val newData = transform(_data.value)
        _data.value = newData
        viewModelScope.launch(Dispatchers.IO) {
            saver.saveData(newData)
        }
    }

    // Пометить завершенные квесты как просмотренные (убирает красный кружочек)
    fun markCompletedQuestsAsViewed() = update { d ->
        if (d.hasUnreadCompletedQuests) {
            d.copy(hasUnreadCompletedQuests = false)
        } else {
            d
        }
    }

    // Решить экономическую задачку
    fun solvePuzzleQuest(questId: Int, selectedAnswer: String): Boolean {
        val currentQuest = _data.value.quests.find { it.id == questId } ?: return false
        if (currentQuest.correctAnswer == selectedAnswer) {
            update { d ->
                val updatedQuests = d.quests.map { q ->
                    if (q.id == questId) {
                        q.copy(
                            questProgress = q.targetValue,
                            questStatus = QuestCategory.COMPLETED
                        )
                    } else q
                }.toMutableList()

                d.copy(
                    quests = updatedQuests,
                    questCompleted = d.questCompleted + 1,
                    hasUnreadCompletedQuests = true
                )
            }
            return true
        }
        return false
    }

    // Завершить день: падение статов на 25, начисление +100 монет, сохранение плана бюджета, сброс расходов
    fun finishDay(food: Int = 0, care: Int = 0, mood: Int = 0) = update { d ->
        var completedCount = 0

        // Прогресс квеста "Заверши 2 дня"
        val updatedQuests = QuestManager.processEvent(d.quests, QuestType.FINISH_DAYS, 1) { _, _ ->
            completedCount++
        }

        val updatedNeeds = d.petNeeds.toMutableList().also {
            if (it.size >= 3) {
                it[0] = (it[0] - 25).coerceAtLeast(0)
                it[1] = (it[1] - 25).coerceAtLeast(0)
                it[2] = (it[2] - 25).coerceAtLeast(0)
            }
        }

        d.copy(
            money = d.money + 100,
            budget = mutableListOf(food, care, mood),
            expenses = mutableListOf(0, 0, 0),
            lastFinishDayTime = System.currentTimeMillis(),
            petNeeds = updatedNeeds,
            quests = updatedQuests,
            questCompleted = d.questCompleted + completedCount,
            hasUnreadCompletedQuests = d.hasUnreadCompletedQuests || completedCount > 0
        )
    }

    // Пополнить копилку
    fun depositToBank(amount: Int, goalTarget: Int) = update { d ->
        val actualAmount = amount.coerceAtMost(d.money)
        if (actualAmount <= 0) return@update d

        var completedCount = 0

        // Прогресс квеста "Отложи монет в копилку"
        val currentQuests = QuestManager.processEvent(d.quests, QuestType.DEPOSIT_TO_BANK, actualAmount) { _, _ ->
            completedCount++
        }

        val newBank = d.bank + actualAmount
        val newMoney = d.money - actualAmount

        if (newBank >= goalTarget) {
            // При достижении цели даем много опыта и повышаем уровень!
            val questsWithBonus = currentQuests.map { q ->
                if (q.questStatus == QuestCategory.UNCOMPLETED) {
                    q.copy(
                        questProgress = q.targetValue,
                        questStatus = QuestCategory.COMPLETED
                    )
                } else q
            }.toMutableList()

            d.copy(
                money = newMoney,
                bank = (newBank - goalTarget).coerceAtLeast(0),
                petStage = d.petStage + 1,
                questCompleted = d.questCompleted + 3 + completedCount,
                quests = questsWithBonus,
                hasUnreadCompletedQuests = true
            )
        } else {
            d.copy(
                money = newMoney,
                bank = newBank,
                quests = currentQuests,
                questCompleted = d.questCompleted + completedCount,
                hasUnreadCompletedQuests = d.hasUnreadCompletedQuests || completedCount > 0
            )
        }
    }

    // Пополнение / списание средств
    fun changeMoney(delta: Int) = update { it.copy(money = it.money + delta) }

    fun buyItem(itemId: Int, price: Int, stats: MutableList<Int>) = update { d ->
        val dominantStatIndex = when {
            stats[0] >= stats[1] && stats[0] >= stats[2] -> 0 // Еда
            stats[1] >= stats[0] && stats[1] >= stats[2] -> 1 // Уход
            else -> 2 // Настроение
        }

        var completedCount = 0

        // 1. Прогресс общего расхода средств
        var updatedQuests = QuestManager.processEvent(d.quests, QuestType.SPEND_TOTAL_MONEY, price) { _, _ ->
            completedCount++
        }

        // 2. Прогресс расхода по конкретным категориям (Еда / Уход)
        if (dominantStatIndex == 0 && stats[0] > 0) {
            updatedQuests = QuestManager.processEvent(updatedQuests, QuestType.SPEND_ON_FOOD, price) { _, _ ->
                completedCount++
            }
        } else if (dominantStatIndex == 1 && stats[1] > 0) {
            updatedQuests = QuestManager.processEvent(updatedQuests, QuestType.SPEND_ON_CARE, price) { _, _ ->
                completedCount++
            }
        }

        val updatedExpenses = d.expenses.toMutableList().also {
            while (it.size < 3) it.add(0)
            it[dominantStatIndex] += price
        }

        val updatedInventory = d.inventory.toMutableList().also {
            if (itemId - 1 in it.indices) {
                it[itemId - 1] += 1
            }
        }

        val updatedNeeds = d.petNeeds.toMutableList().also {
            if (it.size >= 3) {
                it[0] = (it[0] + stats[0]).coerceAtMost(100)
                it[1] = (it[1] + stats[1]).coerceAtMost(100)
                it[2] = (it[2] + stats[2]).coerceAtMost(100)
            }
        }

        d.copy(
            money = d.money - price,
            monetSpened = d.monetSpened + price,
            expenses = updatedExpenses,
            inventory = updatedInventory,
            petNeeds = updatedNeeds,
            quests = updatedQuests,
            questCompleted = d.questCompleted + completedCount,
            hasUnreadCompletedQuests = d.hasUnreadCompletedQuests || completedCount > 0
        )
    }

    // Забрать награду за квест при ручном нажатии
    fun claimQuestReward(questId: Int) = update { d ->
        val quest = d.quests.find { it.id == questId }
        if (quest != null && quest.questProgress >= quest.targetValue && quest.questStatus == QuestCategory.UNCOMPLETED) {
            val updatedQuests = d.quests.map { q ->
                if (q.id == questId) q.copy(questStatus = QuestCategory.COMPLETED) else q
            }.toMutableList()

            d.copy(
                questCompleted = d.questCompleted + 1,
                quests = updatedQuests,
                hasUnreadCompletedQuests = true
            )
        } else {
            d
        }
    }

    // Функционал квестов
    fun initQuests() = update { it.copy(quests = questsItems.toMutableList()) }

    fun addQuest(quest: Quests) = update { d ->
        d.copy(quests = (d.quests + quest).toMutableList())
    }

    fun changeQuestCompletion(id: Int) = update { d ->
        val updatedQuests = d.quests.map { q ->
            if (q.id == id) {
                val newStatus = if (q.questStatus == QuestCategory.UNCOMPLETED) {
                    QuestCategory.COMPLETED
                } else {
                    QuestCategory.UNCOMPLETED
                }
                q.copy(questStatus = newStatus)
            } else {
                q
            }
        }.toMutableList()
        d.copy(quests = updatedQuests)
    }

    fun resetData() = update {
        val newData = Data(quests = questsItems.toMutableList())
        newData
    }

    fun finishFirstStart() = update { d ->
        d.copy(firstStart = false)
    }

    fun setPetName(name: String) = update { d ->
        d.copy(petName = name)
    }

    fun setPetType(type: Int) = update { d ->
        d.copy(petType = type)
    }
}
