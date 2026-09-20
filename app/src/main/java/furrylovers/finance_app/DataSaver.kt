package furrylovers.finance_app
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.content.Context
import android.text.BoringLayout
import java.io.File
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

enum class TaskDifficulty(val reward: Int) {
    easy(30),
    medium(60),
    hard(100)
}

enum class MiniGameType(val reward: Int) {
    shashki(40),
    cheburek(70),
    denis(90)
}

@Serializable
data class Data(
    var firstStart: Boolean = true,

    var petName: String = "PetName",
    var petType: Byte = 1,
    var petStage: Int = 1,
    var petNeeds: MutableList<Int> = mutableListOf(0,0,0), //Food, Care, Mood

    var money: Int = 500, //базовое кол-во
    var inventory: MutableList<Int> = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0), //вписать сюда чо тут есть я хз
    var history: MutableList<String> = mutableListOf(),
    var startMoneyGiven: Boolean = false

) {
//
//    НУЖНО БУДЕТ ПЕРЕДЕЛАТЬ ЧТОБЫ ВСЯКАЯ ФИГНЯ ХРАНИЛАСЬ В ОЗУ И СОХРАНЯЛАСЬ ТОЛЬКО КОГДА ПРИЛОЖЕНИЕ СВОРАЧИВАЕТСЯ
//
    fun isFirstStart(context: Context): Boolean {
        val data = DoJson(context).loadData()
        return data.firstStart
    }
    fun finishFirstStart(context: Context) {
        val data = DoJson(context).loadData()
        data.firstStart = false
        DoJson(context).saveData(data)
    }
    fun changePetName(context: Context, name: String) {
        val data = DoJson(context).loadData()
        data.petName = name
        DoJson(context).saveData(data)
    }

    fun changePetType(context: Context, type: Byte) {
        val data = DoJson(context).loadData()
        data.petType = type
        DoJson(context).saveData(data)
    }

    fun changePetStage(context: Context, stage: Int) {
        val data = DoJson(context).loadData()
        data.petStage = stage
        DoJson(context).saveData(data)
    }

    //Сюда передаем не конкретное значение а его изменение
    //те если нужно уменьшить на 10, то передаем -10 и так далее
    fun changeMoney(context: Context, money: Int) {
        val data = DoJson(context).loadData()
        data.money += money
        DoJson(context).saveData(data)
    }

    //Сюда передаем не конкретное значение а его изменение
    fun changePetNeeds(context: Context, food: Int = 0, care: Int = 0, mood: Int = 0) {
        val data = DoJson(context).loadData()
        data.petNeeds = mutableListOf(data.petNeeds[0]+food, data.petNeeds[1]+care, data.petNeeds[2]+mood)
        DoJson(context).saveData(data)
    }

    //Сюда передаем не конкретное значение а его изменение
    fun changeInventory(context: Context, index: Int, change: Int) {
        val data = DoJson(context).loadData()
        data.inventory[index] += change
        DoJson(context).saveData(data)
    }
    //начальные бабосики ((проверка флага, чтобы повторно не начислить))
    fun giveStartMoney(context: Context) {
        val data = DoJson(context).loadData()
        if (data.startMoneyGiven) return
        data.money += 500
        data.startMoneyGiven = true
        data.history.add("Старт +500")
        DoJson(context).saveData(data)
    }

    // за отыгранный период (день, либо неделю)
    fun givePeriod(context: Context) {
        val data = DoJson(context).loadData()
        data.money += 250
        data.history.add("Период +250")
        DoJson(context).saveData(data)
    }

    // за ежедневный вход бонус
    fun giveBonus(context: Context) {
        val data = DoJson(context).loadData()
        data.money += 20
        data.history.add("Бонус за вход +20")
        DoJson(context).saveData(data)
    }

    // сюда передаем сложность задания в классе (easy,medium,hard)
    fun giveTaskReward(context: Context, difficulty: TaskDifficulty) {
        val data = DoJson(context).loadData()
        val reward = difficulty.reward
        data.money += reward
        data.history.add("Задание (${difficulty.name}) $reward")
        DoJson(context).saveData(data)
    }

    //сюда передаем тип мини-игры (Shashka/cheburek/denis)
    fun giveMiniGameReward(context: Context, game: MiniGameType) {
        val data = DoJson(context).loadData()
        val reward = game.reward
        data.money += reward
        data.history.add("Мини-игра (${game.name}) $reward")
        DoJson(context).saveData(data)
    }

    //сюда передаем конкретную сумму награды и истоочник(За что даем), если сумма будет <= 0 , то ничего не начислится
    fun giveCustomReward(context: Context, reward: Int, source: String) {
        if (reward <= 0) return
        val data = DoJson(context).loadData()
        data.money += reward
        data.history.add("$source $reward")
        DoJson(context).saveData(data)
    }
    //сюда передаем цену и кол-во еды, если денег не хватает то анлак братва
    fun buyFood(context: Context, price: Int, foodAmount: Int): Boolean {
        val data = DoJson(context).loadData()
        if (price <= 0 || foodAmount <= 0) return false
        if (data.money < price) return false
        data.money -= price
        data.petNeeds = mutableListOf(
            data.petNeeds[0] + foodAmount,
            data.petNeeds[1],
            data.petNeeds[2]
        )
        data.history.add("Покупка еды -$price (+$foodAmount еды)")
        DoJson(context).saveData(data)
        return true
    }

    //покупка ухода (лечение, гигиена)
    fun buyCare(context: Context, price: Int, careAmount: Int): Boolean {
        val data = DoJson(context).loadData()
        if (price <= 0 || careAmount <= 0) return false
        if (data.money < price) return false
        data.money -= price
        data.petNeeds = mutableListOf(
            data.petNeeds[0],
            data.petNeeds[1] + careAmount,
            data.petNeeds[2]
        )
        data.history.add("Покупка ухода -$price (+$careAmount ухода)")
        DoJson(context).saveData(data)
        return true
    }

    //покупка развлечения для настроения питомца
    fun buyMood(context: Context, price: Int, moodAmount: Int): Boolean {
        val data = DoJson(context).loadData()
        if (price <= 0 || moodAmount <= 0) return false
        if (data.money < price) return false
        data.money -= price
        data.petNeeds = mutableListOf(
            data.petNeeds[0],
            data.petNeeds[1],
            data.petNeeds[2] + moodAmount
        )
        data.history.add("Покупка развлечения -$price (+$moodAmount настроения)")
        DoJson(context).saveData(data)
        return true
    }

    //сюда передаём индекс предмета в inventory и цену
    fun buyInventoryItem(context: Context, index: Int, price: Int): Boolean {
        val data = DoJson(context).loadData()
        if (index < 0 || index >= data.inventory.size) return false
        if (price <= 0) return false
        if (data.money < price) return false

        data.money -= price
        data.inventory[index] += 1
        data.history.add("Покупка предмета #$index -$price")
        DoJson(context).saveData(data)
        return true
    }

    //кастомная трата если нужна будет покупка, которой нет в функциях выше то вот братва, сюда передаём сумму и источник (за что списываем)
    fun spendCustomMoney(context: Context, amount: Int, source: String): Boolean {
        if (amount <= 0) return false
        val data = DoJson(context).loadData()
        if (data.money < amount) return false
        data.money -= amount
        data.history.add("$source -$amount")
        DoJson(context).saveData(data)
        return true
    }
}

class DoJson(private val context: Context) { //переименовать как нибудь

    private val path = File(context.filesDir,"data.json")

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun saveData(data: Data) {
        path.writeText(json.encodeToString(Data.serializer(), data))
    }

    fun loadData(): Data {
        val restoredData = json.decodeFromString<Data>(path.readText())
        return restoredData
    }

    //использовать только для отладки / демонстрации проекта
    fun loadDataFromFile(filePath: File): Data {
        val data = json.decodeFromString<Data>(filePath.readText())
        return data
    }

    //использовать только для отладки
    fun saveDataToFile(data: Data, filePath: File) {
        filePath.writeText(json.encodeToString(Data.serializer(), data))
    }

    fun deleteData(){
        val newData = Data()
        path.writeText(json.encodeToString(Data.serializer(), newData))
    }
}
