package furrylovers.finance_app
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.content.Context
import java.io.File

@Serializable
data class Data(
    var petName: String = "PetName",
    var petStage: Int = 1,
    var petNeeds: MutableList<Int> = mutableListOf(0,0,0), //Food, Care, Fun

    var money: Int = 0,
    var inventory: MutableList<Int> = mutableListOf(0,0,0,0,0,0,0,0) //вписать сюда чо тут есть я хз
)

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

    fun deleteData(): Data {
        val newData = Data()
        json.encodeToString(Data.serializer(), newData)
        return newData
    }
}