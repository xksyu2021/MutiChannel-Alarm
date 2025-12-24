package com.example.mutichannel_alarm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme
import kotlin.getValue

class AlarmGet : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("ALARM_ID",-1)
        val alarmViewModel: AlarmViewModel by viewModels {
            val repository = (application as MCApplication).repository
            AlarmViewModelFactory(id,repository)
        }

        setContent {
            ContrastAwareReplyTheme{
                alarmGetPage(alarmViewModel)
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun alarmGetPage(alarmViewModel: AlarmViewModel? = null){
    val temp = AlarmTemp()
    val alarmById = alarmViewModel?.alarmById?.collectAsState()
    LaunchedEffect(alarmById) {
        alarmById?.value?.let { alarm ->
            temp.hour.value = alarm.timeHour
            temp.minute.value = alarm.timeMinute
            temp.text.value = alarm.name
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = String.format(
                    "%02d : %02d",
                    temp.hour.value,
                    temp.minute.value
                ),
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(Modifier.padding(vertical = 15.dp))
            Text(
                text = temp.text.value ,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.padding(vertical = 100.dp))
            Button(onClick = {

            }) {
                Text(text="Ring again",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(Modifier.padding(vertical = 40.dp))
            OutlinedButton(onClick = {

            }) {
                Text(text="Stop",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun pre(){
    ContrastAwareReplyTheme{
        alarmGetPage()
    }
}