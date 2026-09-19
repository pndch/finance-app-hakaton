package furrylovers.finance_app

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.nio.file.WatchEvent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import furrylovers.finance_app.ui.theme.ui.theme.FinanceappTheme
import androidx.compose.foundation.layout.Row

class AdultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceappTheme {
                    AdultMenu()
                }
            }
        }
    }


@Composable
fun AdultMenu() {
    val context = LocalContext.current
    val activity = context as? Activity
    //var data = DoJson(context).loadData()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(modifier = Modifier.padding(vertical = 48.dp), text = "Цели приложения")

        Text(modifier = Modifier.padding(vertical = 48.dp), text = "Пройденные темы")

        Text(modifier = Modifier.padding(vertical = 48.dp), text = "Прогресс")
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Button(
            modifier = Modifier.padding(horizontal = 8.dp),
            onClick = {
                context.startActivity(Intent(context, TitleScreenActivity::class.java))
                activity?.finish()
            }
        ) {
            Text(text = "Вернуться")
        }

        Button(
            onClick = {
                DoJson(context).deleteData()
            }
        ) {
            Text(text = "Сбросить прогресс")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdultPreview() {
    FinanceappTheme {
        AdultMenu()
    }
}