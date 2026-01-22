package org.xksyu.mca.page

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.xksyu.mca.MainActivity
import org.xksyu.mca.R
import org.xksyu.mca.data.base.AlarmData
import org.xksyu.mca.data.prefer.SettingsManager
import org.xksyu.mca.feature.basic.setAlarm
import org.xksyu.mca.feature.permission.Permission
import org.xksyu.mca.feature.permission.ShizukuActivate
import org.xksyu.mca.ui.theme.ContrastAwareReplyTheme

data class Page(var step: MutableState<Int> = mutableIntStateOf(0))
class ActivateActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        enableEdgeToEdge()
        val page = Page()
        val per = Permission(context = this)
        per.permissionCheck()

        setContent {
            ContrastAwareReplyTheme{
                when(page.step.value) {
                    0 -> ActivatePageA(
                        onBackA = {
                            settingsManager.debugSet(SettingsManager.DEBUG_GRANT)
                            finish()
                        },
                        onBackB = {
                            settingsManager.debugSet(SettingsManager.DEBUG_GRANT)
                            page.step.value = 1
                        },
                        context = this, activity = this, settingsManager = settingsManager, intent = intent, per = per)
                    1 -> ActivatePageGuide(
                        onBackA = {
                            page.step.value = 0
                        },
                        onBackB = {
                            settingsManager.notFirst()
                            settingsManager.waySet(SettingsManager.WAY_DEFAULT)
                            Toast.makeText(this, R.string.actPage_shizuku_finish, Toast.LENGTH_SHORT).show()
                            finish()
                            val intent = Intent(this, MainActivity::class.java)
                            this.startActivity(intent)
                        },
                        context = this, settingsManager, page = page)
                    2 -> {ActivatePageShizuku(
                        onBackA = {
                            page.step.value = 1
                            settingsManager.debugSet(SettingsManager.DEBUG_GRANT)
                        },
                        onBackB = {
                            settingsManager.notFirst()
                            settingsManager.waySet(SettingsManager.WAY_SHIZUKU)
                            settingsManager.debugSet(SettingsManager.DEBUG_GRANT)
                            finish()
                            val intent = Intent(this, MainActivity::class.java)
                            this.startActivity(intent)
                        },
                        context = this)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivatePageGuide(onBackA: () -> Unit = {}, onBackB: () -> Unit = {}, context: Context, settingsManager: SettingsManager, page: Page) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 30.dp)
    ){
        BackHandler(enabled = true) {
            onBackA()
        }

        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer),
            onClick = onBackB,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.actPage_select_def_t),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.actPage_select_def_c),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer),
            onClick = {
                page.step.value = 2
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.actPage_select_shizuku_t),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.actPage_select_shizuku_c),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun ActivatePageShizuku(context: Context, onBackA: () -> Unit = {}, onBackB: () -> Unit = {}){
    BackHandler(enabled = true) {
        onBackA()
    }
    val shizukuGrant = ShizukuActivate()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 30.dp)
    ) {
        shizukuGrant.CheckPermissionUI(context,onBackA,onBackB)
    }
}

@Composable
fun ActivatePageA (onBackA: () -> Unit = {}, onBackB: () -> Unit = {},context: Context, activity: Activity,settingsManager : SettingsManager,intent : Intent,per: Permission) {
    val grantValue = remember {
        val value = intent.getBooleanExtra("GRANT", false)
        if (value) {
            per.lockScreen.value = true
        }
        value
    }
    val lockScreenState = remember { per.lockScreen }
    val lockScreen by lockScreenState

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
            dismissButton = {
                OutlinedButton(onClick = { showCheck = false }) {
                    Text(stringResource(R.string.actPage_onBack_continue))
                }
            },
            confirmButton = {
                Button(onClick = {
                    showCheck = false
                    onBackA()
                }
                ) {
                    Text(stringResource(R.string.actPage_onBack_back))
                }
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                per.permissionCheck()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                if(!per.exactAlarm.value && Build.VERSION.SDK_INT >= 31) {
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        intent.data = "package:${context.packageName}".toUri()
                        intent.putExtra("GRANT", true)
                        context.startActivity(intent)
                    }) {
                        Text(stringResource(R.string.actPage_grant))
                    }
                }else{
                    Button(onClick = {
                        intent.putExtra("GRANT", true)
                    },
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

        var show by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.actPage_permission_only),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.padding(vertical = 8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)){
                Card(modifier = Modifier
                    .padding(5.dp)
                    .background(Color.Transparent),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ){
                    Column(modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp)
                        .fillMaxWidth(0.95f)){
                        Text(
                            stringResource(R.string.actPage_permission_bg_t),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            stringResource(R.string.actPage_permission_bg_c),
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                            Button(
                                onClick = {
                                    openUrl(context = context, url = "https://xksyu.online")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.error,
                                )
                            ) {
                                Text(stringResource(R.string.actPage_textB))
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                            OutlinedButton(onClick = {
                                val intentB = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intentB.data = "package:${context.packageName}".toUri()
                                context.startActivity(intentB)
                            }) {
                                Text(stringResource(R.string.actPage_grant))
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
                            stringResource(R.string.actPage_permission_ls_t),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            stringResource(R.string.actPage_permission_ls_c),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if(!lockScreen){
                                Button(onClick = {
                                    show = true
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
                }
            }
        }

        if(show){
            AlertDialog(
                onDismissRequest = { },
                text = { Text(stringResource(R.string.actPage_permission_lc_text)) },
                dismissButton = {
                    OutlinedButton(onClick = {
                        show = false
                    }
                    ) { Text(stringResource(R.string.actPage_permission_lc_back)) }
                },
                confirmButton = {
                    Button(onClick = {
                        show = false
                        settingsManager.debugSet(SettingsManager.DEBUG_GRANT)
                        val alarm = AlarmData(
                            settingsManager.updateId(),
                            "Click",
                            0,
                            0,
                            AlarmData.AUTO_ONCE,
                            0,
                            false,
                            0,
                            0,
                            isOpen = true,
                            isRepeat = true
                        )
                        setAlarm(alarm,context,settingsManager)

//                        val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
//                        context.startActivity(intent)
                    }
                    ) { Text(stringResource(R.string.actPage_permission_lc_start)) }
                }
            )
        }

        Spacer(Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    onBackB()
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