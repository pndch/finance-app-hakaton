package furrylovers.finance_app
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Data(
    var petName: String,
    var petStage: Int = 1,
    var petNeeds: MutableList<Int> = mutableListOf(0,0,0), //Food, Care, Fun

    var money: Int = 0,
    var inventory: MutableList<Int> = mutableListOf(0,0,0,0,0,0,0,0) //вписать сюда чо тут есть я хз

)

class SaveJson() {
    private val path = File("data.json")

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun saveData(data: Data) {
        path.writeText(json.encodeToString(Data.serializer(), data))
    }

    fun loadData(): Data {
        val readString = path.readText()
        val restoredData = json.decodeFromString<Data>(readString)
        return restoredData
    }
}