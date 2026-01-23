package org.xksyu.mca.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xksyu.mca.MCApplication
import org.xksyu.mca.MainActivity
import org.xksyu.mca.R
import org.xksyu.mca.data.base.AlarmData
import org.xksyu.mca.data.base.AlarmViewModel
import org.xksyu.mca.data.base.AlarmViewModelFactory
import org.xksyu.mca.data.prefer.SettingsManager
import org.xksyu.mca.debug.PopupDebug
import org.xksyu.mca.feature.basic.AlarmTemp
import org.xksyu.mca.feature.basic.Reminder
import org.xksyu.mca.feature.basic.repeatFun
import org.xksyu.mca.feature.basic.setAlarm
import org.xksyu.mca.ui.theme.ContrastAwareReplyTheme

class AlarmGet : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            //WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            //WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            //or WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val id = intent.getIntExtra("ALARM_ID",-1)
        settingsManager = SettingsManager(this)

        println("----------------AlarmGet---------------")
        println("DEBUG ALARM_ID in AlarmGetPage = $id")
        val alarmViewModel: AlarmViewModel by viewModels {
            val repository = (application as MCApplication).repository
            AlarmViewModelFactory(id, repository)
        }

        val reminder = Reminder(this,settingsManager,id)
        reminder.start()
        reminder.idleAction{
            repeatFun(AlarmData.REPEAT_AUTO,this,alarmViewModel,settingsManager)
            reminder.stop()
            finish()
        }

        setContent {
            ContrastAwareReplyTheme{
                if(settingsManager.debugGet() != SettingsManager.DEBUG_OFF){
                    PopupDebug(
                        alarmViewModel = alarmViewModel,
                        onFinish = { grantValue ->
                            if(settingsManager.debugGet() == SettingsManager.DEBUG_GRANT) {
                                val intent = Intent(this, ActivateActivity::class.java)
                                intent.putExtra("GRANT", grantValue)
                                this.startActivity(intent)
                            } else {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("GRANT", grantValue)
                                this.startActivity(intent)
                            }

                            reminder.stop()
                            finish()
                        },
                        settingsManager = settingsManager,
                        context = this,
                        intent = intent
                    )
                }else {
                    AlarmGetPage(
                        alarmViewModel = alarmViewModel,
                        onFinish = {
                            reminder.stop()
                            finish()
                        },
                        settingsManager = settingsManager,
                        context = this
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AlarmGetPage(alarmViewModel: AlarmViewModel, onFinish: () -> Unit = {}, settingsManager: SettingsManager, context: Context){
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
                    if(!it.isRepeat && it.autoWeek!=0) setAlarm(it, context,settingsManager)
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
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(Modifier.padding(vertical = 100.dp))
            Button(onClick = {
                repeatFun(AlarmData.REPEAT_MANUAL,context,alarmViewModel,settingsManager)
                onFinish()
            }) {
                Text(text= stringResource(R.string.getPage_again),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(Modifier.padding(vertical = 40.dp))
            OutlinedButton(onClick = {
                onFinish()
            }) {
                Text(text= stringResource(R.string.getPage_close),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}