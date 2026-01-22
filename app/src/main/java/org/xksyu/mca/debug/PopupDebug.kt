package org.xksyu.mca.debug

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xksyu.mca.R
import org.xksyu.mca.data.base.AlarmViewModel
import org.xksyu.mca.data.prefer.SettingsManager
import org.xksyu.mca.feature.basic.setAlarm

@Composable
fun PopupDebug(alarmViewModel: AlarmViewModel, onFinish: (grantValue: Boolean) -> Unit = {}, settingsManager: SettingsManager, context: Context,intent: Intent){
    BackHandler(true) { }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(vertical = 60.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(vertical = 30.dp)
                        .wrapContentHeight()
                        .background(Color.Transparent),
                ) {
                    Text(stringResource(R.string.debug_grant))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end=5.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ElevatedButton(
                            onClick = {
                                onFinish(true)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.tertiary,
                            )
                        ) {
                            Text(stringResource(R.string.debug_grantSuccess))
                        }
                    }
                    Spacer(Modifier.padding(vertical = 20.dp))
                    Text(stringResource(R.string.debug_grantFailed) )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end=5.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ElevatedButton(
                            onClick = {
                                onFinish(false)
                            },
                        ) {
                            Text(stringResource(R.string.actPage_permission_lc_back))
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.padding(vertical = 70.dp))
            Text("DEBUG MODE",style = MaterialTheme.typography.headlineSmall)
            val alarmById by alarmViewModel.alarmById.collectAsState()
            alarmById?.let {
                Text(
                    stringResource(
                        R.string.debug_alarmData,
                        it.id,
                        it.name, it.timeHour, it.timeMinute,
                        it.weekSelect, it.remind, it.remindTime, it.remindMinute,
                        it.isRepeat
                    )
                )
            }
            Row(Modifier.padding(10.dp)) {
                OutlinedButton(onClick = {
                    alarmById?.let {
                        val repeatAlarm = it.copy(
                            id = settingsManager.updateId(),
                            isRepeat = true, autoWeek = 0
                        )
                        setAlarm(repeatAlarm, context, settingsManager)
                        alarmViewModel.insert(repeatAlarm)
                    }
                    onFinish(false)
                }
                ) { Text(text = "AGAIN") }
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                OutlinedButton(onClick = { onFinish(false) }) { Text(text = "OFF") }
            }
        }
    }
}