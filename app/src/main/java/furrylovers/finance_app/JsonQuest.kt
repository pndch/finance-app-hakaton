package furrylovers.finance_app
import kotlinx.serialization.json.Json
import android.content.Context
import java.io.File

class JsonQuest(private val context: Context) {
    private val path = File(context.filesDir,"quest.json")

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun initQuest() {
        val questlist = QuestsList()
        for (i in 0..9) {
            questlist.quests[i] = questsItems[i]
        }
        path.writeText(json.encodeToString(QuestsList.serializer(), questlist))
    }

    fun addQuest(quest: Quests) {
        var questlist = loadQuest()
        questlist.quests.add(quest)
        path.writeText(json.encodeToString(QuestsList.serializer(), questlist))
    }

    fun loadQuest(): QuestsList {
        try {
            if (path.exists()) {
                return json.decodeFromString<QuestsList>(path.readText())
            } else {
                JsonQuest(context).initQuest()
                return json.decodeFromString<QuestsList>(path.readText())
            }
        } catch (e: Exception) {
            JsonQuest(context).initQuest()
            return json.decodeFromString<QuestsList>(path.readText())
        }
    }

    fun changeComplition(id: Int) {
        var questlist = loadQuest()
        questlist.quests[id].questStatus = if (questlist.quests[id].questStatus == QuestCategory.UNCOMPLETED) {
            QuestCategory.COMPLETED
        } else {
            QuestCategory.UNCOMPLETED }
        path.writeText(json.encodeToString(QuestsList.serializer(), questlist))
    }

    //использовать только для отладки / демонстрации проекта
    fun loadQuestFromFile(filePath: File): QuestsList {
        val quest = json.decodeFromString<QuestsList>(filePath.readText())
        return quest
    }

    //использовать только для отладки
    fun saveQuestToFile(quest: QuestsList, filePath: File) {
        filePath.writeText(json.encodeToString(QuestsList.serializer(), quest))
    }

    fun deleteQuest(){
        val newQuestList = QuestsList()
        path.writeText(json.encodeToString(QuestsList.serializer(), newQuestList))
    }
}