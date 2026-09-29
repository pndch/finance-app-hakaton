package furrylovers.finance_app.data_save

import kotlinx.serialization.Serializable
import android.content.Context
import furrylovers.finance_app.quests.Quests
import furrylovers.finance_app.R
import furrylovers.finance_app.quests.questsItems

object DebugSettings {
    var finishDayIntervalMs: Long = 12 * 60 * 60 * 1000L // 12 часов по умолчанию
}

val characters: List<Int> = listOf(
    R.drawable.char0,
    R.drawable.char1,
    R.drawable.char2,
    R.drawable.char3,
    R.drawable.char4,
    R.drawable.char5
)


@Serializable
data class Goal(
    var goalName: String = "Name",
    var goalSize: Int = 0,
    var isComplete: Boolean = false
)

@Serializable
data class Data(
    var firstStart: Boolean = true,
    var hasSeenTutorial: Boolean = false,

    var petName: String = "PetName",
    var petType: Int = 0,
    var petStage: Int = 1,
    var petNeeds: MutableList<Int> = mutableListOf(50, 50, 50), // Food, Care, Mood

    var money: Int = 500,
    var bank: Int = 0,
    var inventory: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),

    // Добавить статистики и подвязать к функциям изменения
    var questCompleted: Int = 0,
    var targetPassed: Int = 0,
    var monetSpened: Int = 0,

    var lastFinishDayTime: Long = 0L,

    var budget: MutableList<Int> = mutableListOf(0, 0, 0), // Еда, Уход, Настроение
    var expenses: MutableList<Int> = mutableListOf(0, 0, 0), // Еда, Уход, Настроение
    var hasUnreadCompletedQuests: Boolean = false, // Непросмотренный красный кружочек на иконке квестов

    var level3EventShown: Boolean = false,
    var level5EventShown: Boolean = false,
    var level3EventCompleted: Boolean = false,
    var level5EventCompleted: Boolean = false,

    var goals: MutableList<Goal> = emptyList<Goal>().toMutableList(),
    var quests: MutableList<Quests> = questsItems.toMutableList()
) {
    fun isFirstStart(context: Context): Boolean {
        val data = JsonData(context).loadData()
        return data.firstStart
    }

    fun finishFirstStart(context: Context) {
        val data = JsonData(context).loadData()
        data.firstStart = false
        JsonData(context).saveData(data)
    }

    fun changePetName(context: Context, name: String) {
        val data = JsonData(context).loadData()
        data.petName = name
        JsonData(context).saveData(data)
    }

    fun changePetType(context: Context, type: Int) {
        val data = JsonData(context).loadData()
        data.petType = type
        JsonData(context).saveData(data)
    }
}
