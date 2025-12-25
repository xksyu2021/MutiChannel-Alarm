package com.xksyu.mutichannel_alarm

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xksyu.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.BackHandler
import java.lang.Runnable

class AlarmGet : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    private val handler = Handler(Looper.getMainLooper())
    private var idleRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("ALARM_ID",-1)
        settingsManager = SettingsManager(this)
        println("----------------AlarmGet---------------")
        println("DEBUG ALARM_ID in AlarmGetPage = $id")
        val alarmViewModel: AlarmViewModel by viewModels {
            val repository = (application as MCApplication).repository
            AlarmViewModelFactory(id,repository)
        }

        idleRunnable = Runnable {
            val alarmRepeat = alarmViewModel.alarmById.value?.copy(
                id = settingsManager.updateId(),
                isRepeat = true, autoWeek = 0
            )
            alarmRepeat?.let {
                if (it.remindTime > 0) {
                    setAlarm(it, this)
                }
                it.remindTime -= 1
            }
            alarmViewModel.update(alarmRepeat)
            idleRunnable?.let { handler.removeCallbacks(it) }
            alarmViewModel.alarmById.value?.let {
                if(it.isRepeat) alarmViewModel.delete(it)
            }
            finish()
        }
        handler.postDelayed(idleRunnable!!, 60 * 1000L)

        setContent {
            ContrastAwareReplyTheme{
                alarmGetPage(alarmViewModel = alarmViewModel,
                    onFinish = {
                        idleRunnable?.let { handler.removeCallbacks(it) }
                        alarmViewModel.alarmById.value?.let {
                            if(it.isRepeat) alarmViewModel.delete(it)
                        }
                        finish()
                    },
                    settingsManager = settingsManager,
                    context = this
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun alarmGetPage(alarmViewModel: AlarmViewModel,onFinish: () -> Unit = {},settingsManager: SettingsManager,context: Context){
    val temp = AlarmTemp()
    val alarmById by alarmViewModel.alarmById.collectAsState()
    var updateStatu by remember { mutableStateOf(false) }
    BackHandler(enabled = true){}
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
            println("****** Recompose Count ******")
            LaunchedEffect(alarmById){
                alarmById?.let {
                    temp.text.value = it.name
                    temp.hour.value = it.timeHour
                    temp.minute.value = it.timeMinute

                    println("====== Call setAlarm for next day BEGIN ======")
                    if(!it.isRepeat && it.autoWeek!=0) setAlarm(it, context)
                    println("====== Call setAlarm for next day FINISHED ======")

                    println("****** GetValue Count ******")
                }
                updateStatu = true
                println("****** LaunchedEffect Count ******")
                println("DEBUG AlarmData values:")
                println("  id: ${alarmById?.id}")
                println("  timeHour: ${alarmById?.timeHour}, timeMinute: ${alarmById?.timeMinute}")
                println("  name: ${alarmById?.name}")
                println("DEBUG AlarmTemp values:")
                println("  hour: ${temp.hour.value}, minute: ${temp.minute.value}")
                println("  text: ${temp.text.value}")
            }

            if (updateStatu) {
                println("****** IntoText Count ******")
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
                    text = temp.text.value,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(Modifier.padding(vertical = 100.dp))
            Button(onClick = {
                alarmById?.let{
                    val repeatAlarm = it.copy(
                        id = settingsManager.updateId(),
                        isRepeat = true, autoWeek = 0
                    )
                    setAlarm(repeatAlarm,context)
                    alarmViewModel.insert(repeatAlarm)
                }
                onFinish()
            }) {
                Text(text="Ring again",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(Modifier.padding(vertical = 40.dp))
            OutlinedButton(onClick = {
                onFinish()
            }) {
                Text(text="Stop",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}