package furrylovers.finance_app

import kotlinx.serialization.json.Json
import android.content.Context
import java.io.File

class JsonData(private val context: Context) {
    private val path = File(context.filesDir, "data.json")

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun saveData(data: Data) {
        path.writeText(json.encodeToString(Data.serializer(), data))
    }

    fun loadData(): Data {
        return try {
            if (path.exists()) {
                val data = json.decodeFromString<Data>(path.readText())
                if (data.quests.isEmpty()) {
                    val updated = data.copy(quests = questsItems.toMutableList())
                    saveData(updated)
                    updated
                } else {
                    data
                }
            } else {
                val initial = Data(quests = questsItems.toMutableList())
                saveData(initial)
                initial
            }
        } catch (e: Exception) {
            val initial = Data(quests = questsItems.toMutableList())
            saveData(initial)
            initial
        }
    }

    // Использовать только для отладки / демонстрации проекта
    fun loadDataFromFile(filePath: File): Data {
        return json.decodeFromString<Data>(filePath.readText())
    }

    fun saveDataToFile(data: Data, filePath: File) {
        filePath.writeText(json.encodeToString(Data.serializer(), data))
    }

    fun deleteData() {
        val newData = Data(quests = questsItems.toMutableList())
        saveData(newData)
    }
}
