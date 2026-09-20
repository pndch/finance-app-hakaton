package furrylovers.finance_app
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.content.Context
import android.text.BoringLayout
import java.io.File

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