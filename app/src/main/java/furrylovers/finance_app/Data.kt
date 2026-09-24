package furrylovers.finance_app
import kotlinx.serialization.Serializable
import android.content.Context

val characters: List<Int> = listOf(
    R.drawable.char0,
    R.drawable.char1,
    R.drawable.char2,
    R.drawable.char3,
    R.drawable.char4,
    R.drawable.char5,
    R.drawable.char6,
    R.drawable.char7,
    R.drawable.char8,
)

val characterThumbnails: List<Int> = listOf(
    R.drawable.char0_thumbnail,
    R.drawable.char1_thumbnail,
    R.drawable.char2_thumbnail,
    R.drawable.char3_thumbnail,
    R.drawable.char4_thumbnail,
    R.drawable.char5_thumbnail,
    R.drawable.char6_thumbnail,
    R.drawable.char7_thumbnail,
    R.drawable.char8_thumbnail,
)

@Serializable
data class Goal(
    var goalName: String = "Name",
    var goalSize: Int = 0,
    var isComplete: Boolean = false
) { }

@Serializable
data class Data(
    var firstStart: Boolean = true,

    var petName: String = "PetName",
    var petType: Int = 0,
    var petStage: Int = 1,
    var petNeeds: MutableList<Int> = mutableListOf(50,50,50), //Food, Care, Mood

    var money: Int = 500,
    var bank: Int = 0,
    var inventory: MutableList<Int> = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0),

    //Добавить статистики и подвзязать к функция изменения
    var questCompleted: Int = 0,
    var targetPassed: Int = 0,
    var monetSpened: Int = 0,

    var budget: MutableList<Int> = mutableListOf(0,0,0), //Mandatory / Non-mandatory / Care or smth
    var goals: MutableList<Goal> = emptyList<Goal>().toMutableList()
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

    fun changePetStage(context: Context, stage: Int) {
        val data = JsonData(context).loadData()
        data.petStage = stage
        JsonData(context).saveData(data)
    }

    //Сюда передаем не конкретное значение а его изменение
    //те если нужно уменьшить на 10, то передаем -10 и так далее
    fun changeMoney(context: Context, money: Int) {
        val data = JsonData(context).loadData()
        data.money += money
        JsonData(context).saveData(data)
    }

    //Сюда передаем не конкретное значение а его изменение
    fun changePetNeeds(context: Context, food: Int = 0, care: Int = 0, mood: Int = 0) {
        val data = JsonData(context).loadData()
        data.petNeeds = mutableListOf(data.petNeeds[0]+food, data.petNeeds[1]+care, data.petNeeds[2]+mood)
        JsonData(context).saveData(data)
    }

    fun setBudget(context: Context, budget: MutableList<Int>) {
        val data = JsonData(context).loadData()
        data.budget = budget
        JsonData(context).saveData(data)
    }
}