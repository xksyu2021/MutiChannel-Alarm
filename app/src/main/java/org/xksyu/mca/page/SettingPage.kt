package org.xksyu.mca.page

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.xksyu.mca.R
import org.xksyu.mca.data.prefer.SettingsManager
import org.xksyu.mca.ui.theme.ContrastAwareReplyTheme


class SettingActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        setContent {
            ContrastAwareReplyTheme{
                SettingPage(onBack = { finish() }, context = this,settingsManager = settingsManager)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(onBack: () -> Unit = {},context: Context? = null,settingsManager: SettingsManager){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.title_SettingPage),
                        modifier = Modifier.fillMaxWidth(0.95f),
                        textAlign = TextAlign.End
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.backHead)
                        )
                    }
                },
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Language(settingsManager)
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp)
            )
            Debug(settingsManager)
        }
    }
}

@Composable
fun Debug(settingsManager: SettingsManager){
    var showMenu by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.settingPage_debug))
        }
        var debug by remember { mutableStateOf("Undefined") }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            debug = when(settingsManager.debugGet()) {
                SettingsManager.DEBUG_GRANT -> "DEBUG_GRANT"
                SettingsManager.DEBUG_NOW -> "DEBUG_NOW"
                SettingsManager.DEBUG_VIEW -> "DEBUG_VIEW"
                SettingsManager.DEBUG_OFF -> "DEBUG_OFF"
                else -> "Undefined"
            }

            Text(debug)
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                ) {
                    DropdownMenuItem(
                        text = { Text("DEBUG_OFF") },
                        onClick = {
                            settingsManager.debugSet(SettingsManager.DEBUG_OFF)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("DEBUG_NOW") },
                        onClick = {
                            settingsManager.debugSet(SettingsManager.DEBUG_NOW)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("DEBUG_VIEW") },
                        onClick = {
                            settingsManager.debugSet(SettingsManager.DEBUG_VIEW)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun Language(settingsManager: SettingsManager){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp)
    ){
        var showMenu by remember { mutableStateOf(false) }
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.settingPage_lang))
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val lang = when(settingsManager.getLang()) {
                SettingsManager.LANG_ZH -> stringResource(R.string.settingPage_lang_zh)
                SettingsManager.LANG_EN -> stringResource(R.string.settingPage_lang_en)
                else -> stringResource(R.string.settingPage_lang_auto)
            }

            Text(lang)
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settingPage_lang_auto)) },
                        onClick = {
                            settingsManager.saveLang(SettingsManager.LANG_AUTO)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settingPage_lang_zh)) },
                        onClick = {
                            settingsManager.saveLang(SettingsManager.LANG_ZH)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settingPage_lang_en)) },
                        onClick = {
                            settingsManager.saveLang(SettingsManager.LANG_EN)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}