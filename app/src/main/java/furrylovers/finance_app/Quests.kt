package furrylovers.finance_app

import kotlinx.serialization.Serializable

@Serializable
data class Quests(
    val questName: String = "",
    val questDescription: String = "",
    val questAward: Int = 0,

    val questProgress: Int = 0, // 0-100 или как хотите ваще
    val questStatus: Boolean = false
) {

}