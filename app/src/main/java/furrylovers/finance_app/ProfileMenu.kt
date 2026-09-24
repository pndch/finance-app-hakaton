package furrylovers.finance_app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import furrylovers.finance_app.ui.theme.MainTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileMenu(viewModel: GameViewModel) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    ProfileMenuContent(data)
}

@Composable
fun ProfileMenuContent(data: Data) {
    val context = LocalContext.current
    val activity = context as? Activity
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            //horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(characters[data.petType]),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 25.dp)
                    .height(100.dp)
                    .width(100.dp),
            )
            Text(
                modifier = Modifier
                    .padding(start = 25.dp)
                    .padding(end = 25.dp),
                text = data.petName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            // СЮДА НАДО ВПИХНУТЬ ПОЛОСКУ С ЕГО ОПЫТОМ
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Денег потрачено: " + data.monetSpened.toString())
            //ИТД
        }
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    context.startActivity(Intent(context, AdultActivity::class.java))
                    activity?.finish()
                },
                modifier = Modifier
                    .padding(bottom = 50.dp),
            ) {
                Text("Раздел для взрослых")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileMenuPreview() {
    MainTheme() {
        ProfileMenuContent(Data())
    }
}