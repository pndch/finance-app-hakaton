package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import furrylovers.finance_app.ui.theme.MainTheme

import androidx.compose.ui.platform.LocalContext

@Composable
fun QuestMenu( viewModel: GameViewModel ) {
    val context = LocalContext.current
    val data = remember { try { DoJson(context).loadData() } catch (e: Exception) { Data() } }
    QuestMenuContent(data)

}

@Composable
fun QuestMenuContent( data: Data ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text("")
    }
}

@Preview(showBackground = true)
@Composable
fun QuestMenuPreview() {
    MainTheme() {
        QuestMenuContent(Data())
    }
}