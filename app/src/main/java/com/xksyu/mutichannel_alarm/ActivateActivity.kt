package com.xksyu.mutichannel_alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.content.ContextCompat.startActivity
import com.xksyu.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme

data class Permission(
    val exactAlarm: MutableState<Boolean> = mutableStateOf(false),
    val noticePermission: MutableState<Boolean> = mutableStateOf(false),
    val fullScreen: MutableState<Boolean> = mutableStateOf(false),
    val openScreen: MutableState<Boolean> = mutableStateOf(false),
    val background: MutableState<Boolean> = mutableStateOf(false),
    val context: Context
){
    fun permissionCheck(){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        exactAlarm.value = canScheduleExactAlarms(alarmManager)

        val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        noticePermission.value = noticeManager.areNotificationsEnabled();
    }
}

class ActivateActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        enableEdgeToEdge()
        setContent {
            ContrastAwareReplyTheme{
                ActivatePage(onBack = { finish() }, context = this, activity = this,settingsManager)
            }
        }
    }
}

@SuppressLint("BatteryLife")
@Composable
fun ActivatePage(onBack: () -> Unit = {}, context: Context, activity: Activity,settingsManager: SettingsManager){
    val per = Permission(context = context)
    per.permissionCheck()

    //per.exactAlarm.value = true //for debug

    var showCheck by remember { mutableStateOf(false) }
    BackHandler(enabled = settingsManager.isFirst()) {
        showCheck = true
    }

    if (showCheck) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.actPage_onBack_title)) },
            text = {
                Text(stringResource(R.string.actPage_onBack_text))
            },
            confirmButton = {
                Button(onClick = { showCheck = false }) {
                    Text(stringResource(R.string.actPage_onBack_continue))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showCheck = false
                    settingsManager.notFirst()
                    onBack()
                }
                ) {
                    Text(stringResource(R.string.actPage_onBack_back))
                }
            }
        )
    }

    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(Modifier.padding(vertical = 20.dp))
        Text(stringResource(R.string.actPage_text))
        Spacer(Modifier.padding(vertical = 10.dp))

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .fillMaxWidth(0.95f)
        ) {
            Text(
                stringResource(R.string.actPage_permission_ea_t),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                stringResource(R.string.actPage_permission_ea_c),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if(!per.exactAlarm.value) {
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        intent.data = android.net.Uri.parse("package:${context?.packageName}")
                        context.startActivity(intent)
                    }) {
                        Text(stringResource(R.string.actPage_grant))
                    }
                }else{
                    Button(onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        )
                    ) {
                        Text(stringResource(R.string.actPage_ok))
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(vertical = 15.dp)
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .fillMaxWidth(0.95f)
        ) {
            Text(
                stringResource(R.string.actPage_permission_notice_t),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                stringResource(R.string.actPage_permission_notice_c),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if(!per.noticePermission.value) {
                    Button(onClick = {
//                        requestPermissions(
//                            activity,
//                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                            CODE_NOTICE
//                        )

                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)

                    }) {
                        Text(stringResource(R.string.actPage_grant))
                    }
                }else{
                    Button(onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        )
                    ) {
                        Text(stringResource(R.string.actPage_ok))
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(vertical = 15.dp)
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .fillMaxWidth(0.95f)
        ) {
            Text(
                stringResource(R.string.actPage_permission_fn_t),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                stringResource(R.string.actPage_permission_fn_c),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if(!per.fullScreen.value){
                    Button(onClick = {

                        try {
                            val intent = Intent().apply {
                                action = Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intentB = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intentB.data = Uri.parse("package:${context.packageName}")
                            context.startActivity(intentB)
                        }

                    }) {
                        Text(stringResource(R.string.actPage_grant))
                    }
                }else{
                    Button(onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        )
                    ) {
                        Text(stringResource(R.string.actPage_ok))
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(vertical = 15.dp)
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .fillMaxWidth(0.95f)
        ) {
            Text(
                stringResource(R.string.actPage_permission_os_t),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                stringResource(R.string.actPage_permission_os_c),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if(!per.openScreen.value){
                    Button(onClick = {

                        try {
                            val intent = Intent().apply {
                                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intentB = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intentB.data = Uri.parse("package:${context.packageName}")
                            context.startActivity(intentB)
                        }

                    }) {
                        Text(stringResource(R.string.actPage_grant))
                    }
                }else{
                    Button(onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        )
                    ) {
                        Text(stringResource(R.string.actPage_ok))
                    }
                }
            }
        }

//        HorizontalDivider(
//            thickness = 2.dp,
//            modifier = Modifier
//                .padding(horizontal = 20.dp)
//                .padding(vertical = 15.dp)
//        )
//
//        Column(
//            modifier = Modifier
//                .wrapContentHeight()
//                .padding(16.dp)
//                .fillMaxWidth(0.95f)
//        ) {
//            Text(
//                stringResource(R.string.actPage_permission_bg_t),
//                style = MaterialTheme.typography.headlineSmall
//            )
//            Text(
//                stringResource(R.string.actPage_permission_bg_c),
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End
//            ) {
//                if(!per.background.value){
//                    Button(onClick = {
//
//                    }) {
//                        Text(stringResource(R.string.actPage_grant))
//                    }
//                }else{
//                    Button(onClick = {},
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                            contentColor = MaterialTheme.colorScheme.tertiary,
//                        )
//                    ) {
//                        Text(stringResource(R.string.actPage_ok))
//                    }
//                }
//            }
//        }

        if(per.exactAlarm.value && per.fullScreen.value && per.noticePermission.value && per.openScreen.value){
            Toast.makeText(context, stringResource(R.string.actPage_ok), Toast.LENGTH_SHORT).show()
            settingsManager.notFirst()
            onBack()
        }

        if(settingsManager.isFirst()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        showCheck = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                    )
                ) {
                    Text(stringResource(R.string.actPage_next))
                }
            }
        }
    }
}