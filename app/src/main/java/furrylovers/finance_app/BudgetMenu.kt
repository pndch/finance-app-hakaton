package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import furrylovers.finance_app.ui.theme.MainTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import furrylovers.finance_app.Data
import furrylovers.finance_app.DoJson
import furrylovers.finance_app.R
import androidx.compose.ui.unit.dp
import android.R.attr.height
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Devices

@Composable
fun BudgetMenu( viewModel: GameViewModel ) {
    val context = LocalContext.current
    val data = remember { try { DoJson(context).loadData() } catch (e: Exception) { Data() } }
    MainTheme() {
        QuestMenuContent(data)
    }
}

@Composable
fun BudgetMenuContent( data: Data ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(R.drawable.photo_budget),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 50.dp)
                .height(150.dp)
                .fillMaxWidth(),

            contentScale = ContentScale.Crop
        )
        Text("")
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9)
@Composable
fun BudgetMenuPreview() {
    MainTheme() {
        BudgetMenuContent(Data())
    }
}