package furrylovers.finance_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        saver.saveData(newData)
    }

    // Пополнение / списание средств
    fun changeMoney(delta: Int) = update { it.copy(money = it.money + delta) }

    fun buyItem(itemId: Int, price: Int, stats: MutableList<Int>) = update { d ->
        d.copy(
            money = d.money - price,
            inventory = d.inventory.toMutableList().also { it[itemId - 1] += 1 },
            petNeeds = d.petNeeds.toMutableList().also {
                it[0] += stats[0]
                it[1] += stats[1]
                it[2] += stats[2]
            },
        )
    }

    // Функционал квестов (ранее был в JsonQuest)
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
