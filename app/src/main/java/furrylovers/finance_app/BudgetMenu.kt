package furrylovers.finance_app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import furrylovers.finance_app.ui.theme.MainTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Devices
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableFloatStateOf
@Composable
fun BudgetMenu( viewModel: GameViewModel ) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    //val data = remember { try { DoJson(context).loadData() } catch (e: Exception) { Data() } }
    MainTheme() {
        BudgetMenuContent(data)
    }
}

@Composable
fun BudgetMenuContent( data: Data ) {
    var mandSliderPosition by remember { mutableFloatStateOf(0.0f) }
    var nonMandSliderPosition by remember { mutableFloatStateOf(0.0f) }
    var thirdSliderPosition by remember { mutableFloatStateOf(0.0f) }

    Column(
        modifier = Modifier
            .height(400.dp)
            .width(250.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.padding(top = 15.dp),
            fontSize = 8.sp,
            text = "Составьте план расходов на следующий период"
        )

        Text(
            //modifier = Modifier
            fontSize = 8.sp,
            text = "На вашем счету сейчас: ${data.money}F"
        )

        Text(
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 10.dp),
            fontSize = 8.sp,
            text = "Сколько потратим на самое необходимое?"
        )
        Slider(
            modifier = Modifier
                .width(150.dp)
                .height(20.dp),
            value = mandSliderPosition,
            onValueChange = { }
        )

        Text(
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 10.dp),
            fontSize = 8.sp,
            text = "Сколько потратим на менее важные вещи?"
        )
        Slider(
            modifier = Modifier
                .width(150.dp)
                .height(20.dp),
            value = nonMandSliderPosition,
            onValueChange = { }
        )

        Text(
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 10.dp),
            fontSize = 8.sp,
            text = "Сколько отложим в копилку?"
        )
        Slider(
            modifier = Modifier
                .width(150.dp)
                .height(20.dp),
            value = thirdSliderPosition,
            onValueChange = { }
        )

        Button(
            modifier = Modifier
                .padding(top = 100.dp),
            onClick = {}
        ) { Text("Подтвердить")}
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