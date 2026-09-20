package furrylovers.finance_app
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.content.Context
import android.text.BoringLayout
import java.io.File

@Serializable
data class Data(
    var firstStart: Boolean = true,

    var petName: String = "PetName",
    var petType: Byte = 1,
    var petStage: Int = 1,
    var petNeeds: MutableList<Int> = mutableListOf(0,0,0), //Food, Care, Mood

    var money: Int = 500, //базовое кол-во
    var inventory: MutableList<Int> = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0) //вписать сюда чо тут есть я хз

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
        try {
            if (path.exists()) {
                return json.decodeFromString<Data>(path.readText())
            } else {
                DoJson(context).saveData(Data())
                return json.decodeFromString<Data>(path.readText())
            }
        } catch (e: Exception) {
            DoJson(context).saveData(Data())
            return json.decodeFromString<Data>(path.readText())
        }
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