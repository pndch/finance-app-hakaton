package furrylovers.finance_app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val saver = DoJson(app)

    private val _data = MutableStateFlow(
        try { saver.loadData() } catch (e: Exception) { Data() }
    )
    val data: StateFlow<Data> = _data.asStateFlow()

    fun update(transform: (Data) -> Data) {
        val newData = transform(_data.value)
        _data.value = newData
        saver.saveData(newData)
    }

    fun changeStats(itemId: Int) = update { d ->
        d.copy(
            petNeeds = d.petNeeds.toMutableList().also { it[itemId - 1] += 10 }
        )
    }
    //пополнение
    fun changeMoney(delta: Int) = update { it.copy(money = it.money + delta) }

    fun buyItem(itemId: Int, price: Int) = update { d ->
        d.copy(
            money = d.money - price,
            inventory = d.inventory.toMutableList().also { it[itemId - 1] += 1 }
        )
    }
}