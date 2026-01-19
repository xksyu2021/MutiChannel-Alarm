package org.xksyu.mca.page

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.xksyu.mca.R
import org.xksyu.mca.data.temp.Permission
import org.xksyu.mca.data.temp.SettingsManager
import org.xksyu.mca.feature.permission.CheckPermission
import org.xksyu.mca.ui.theme.ContrastAwareReplyTheme

data class Page(var step: MutableState<Int> = mutableStateOf(0))
class ActivateActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        enableEdgeToEdge()
        var page = Page()
        setContent {
            ContrastAwareReplyTheme{
                when(page.step.value) {
                    0 -> ActivatePageGuide(
                            onBack = {
                                settingsManager.notFirst()
                                finish()
                            },
                            context = this, settingsManager, page = page
                        )
                    1 -> {ActivatePageA(
                        onBackA = {
                            page.step.value = 0
                        },
                        onBackB = {
                            settingsManager.notFirst()
                            settingsManager.waySet(1)
                            finish()
                        },
                        context = this)
                    }
                    2 -> ActivatePageB(
                        onBackA = {
                            page.step.value = 0
                        },
                        onBackB = {
                            settingsManager.notFirst()
                            settingsManager.waySet(2)
                            finish()
                        },
                        context = this, activity = this
                    )
                }
            }
        }
    }
}

@Composable
fun ActivatePageGuide(onBack: () -> Unit = {}, context: Context, settingsManager: SettingsManager, page: Page) {
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
                    onBack()
                }
                ) {
                    Text(stringResource(R.string.actPage_onBack_back))
                }
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 30.dp)
    ){
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
                page.step.value = 1
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
fun ActivatePageA(context: Context, onBackA: () -> Unit = {}, onBackB: () -> Unit = {}){
    BackHandler(enabled = true) {
        onBackA()
    }
    CheckPermission(context,onBackA,onBackB)
}

@Composable
fun ActivatePageB (onBackA: () -> Unit = {}, onBackB: () -> Unit = {}, context: Context, activity: Activity) {
    val per = Permission(context = context)
    per.permissionCheck()

    BackHandler(enabled = true) {
        onBackA()
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
                if(!per.exactAlarm.value) {
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        intent.data = Uri.parse("package:${context?.packageName}")
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
                if(!per.lockScreen.value){
                    Button(onClick = {
                        val CODE_NOTICE = 1001

                        try {
                            requestPermissions(
                                activity,
                                arrayOf(Manifest.permission.USE_FULL_SCREEN_INTENT),
                                CODE_NOTICE
                            )
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
                stringResource(R.string.actPage_permission_bg_t),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                stringResource(R.string.actPage_permission_bg_c),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if(!per.selfStart.value){
                    Button(onClick = {

                        try {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            intent.data = Uri.parse("package:${context?.packageName}")
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

        Spacer(Modifier.padding(vertical = 20.dp))
        Text(stringResource(R.string.actPage_textB))
        Spacer(Modifier.padding(vertical = 10.dp))

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