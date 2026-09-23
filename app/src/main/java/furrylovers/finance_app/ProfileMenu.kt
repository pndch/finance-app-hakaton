package furrylovers.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import furrylovers.finance_app.ui.theme.MainTheme
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
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
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import furrylovers.finance_app.ui.theme.SuccessGreen

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