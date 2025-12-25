package com.xksyu.mutichannel_alarm

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xksyu.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme

//android main
class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    private val alarmViewModel: AlarmViewModel by viewModels {
        val repository = (application as MCApplication).repository
        AlarmViewModelFactory(repository=repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        setContent {
            ContrastAwareReplyTheme{
                mainPage(settingsManager = settingsManager, alarmViewModel = alarmViewModel, context = this@MainActivity)
            }
        }
    }
}

//the main page index.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainPage(settingsManager :SettingsManager, alarmViewModel: AlarmViewModel, context :Context? = null) {
    var showPage by remember { mutableIntStateOf(0) } //1 for debug. set it as 0 in release.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(R.string.title_mainPage))
                },
                actions = {
                    topMenu(context)
                }
            )
        },
        floatingActionButton = {
            if(showPage == 0){
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, AddActivity::class.java)
                        intent.putExtra("IS_EDIT", false)
                        context?.startActivity(intent)
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Icon(Icons.Filled.Add, stringResource(R.string.page1_addLogo))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    onClick = {
                        showPage = 0
                    },
                    icon = { Icon(Icons.Filled.Notifications, null) },
                    label = { Text(stringResource(R.string.bar_alarm)) },
                    selected = showPage == 0
                )
                NavigationBarItem(
                    onClick = {
                        showPage = 1
                    },
                    icon = { Icon(Icons.Filled.Build, null) },
                    label = { Text(stringResource(R.string.bar_channel)) },
                    selected = showPage == 1
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when(showPage){
                1 -> channelPage(settingsManager = settingsManager)
                else -> alarmPage(alarmViewModel = alarmViewModel,context = context)
            }
        }
    }
}

//menu details of main pages.
@Composable
fun topMenu(context: Context? = null)
{
    var showMenu by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(10.dp)) {
        IconButton(onClick = { showMenu = !showMenu }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menuHead)
            )
        }
        DropdownMenu(
            expanded = showMenu,
            //expanded = true, //true used for debug. set it as showMenu in release.
            onDismissRequest = { showMenu = false },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menuList_setting)) },
                leadingIcon = { Icon(Icons.Filled.Settings, null) },
                onClick = {
                    showMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menuList_about)) },
                leadingIcon = { Icon(Icons.Filled.Info, null) },
                onClick = {
                    showMenu = false
                    val intent = Intent(context, AboutActivity::class.java)
                    context?.startActivity(intent)
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menuList_activate)) },
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                onClick = {
                    showMenu = false
                }
            )
        }
    }
}


//pages
//alarmPage
@SuppressLint("ComposableNaming")
@Composable
fun alarmPage(alarmViewModel: AlarmViewModel,context: Context? = null){
    val scrollState = rememberScrollState()
    val alarms by remember { alarmViewModel.alarms }.collectAsState(initial = emptyList())
    val weekName = arrayOf("Mon","Tue","Wen","Tur","Fri","Sat","Sun")
    val autoWeekName = arrayOf("weekdays","weekends")
    if(alarms.isEmpty()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.page1_empty),
                style = MaterialTheme.typography.headlineLarge,
            )
        }
    }else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for(alarm in alarms) {
                if (alarm.isRepeat) continue
                var open by remember { mutableStateOf(alarm.isOpen) }
                var weekSelect = ""
                when (alarm.autoWeek) {
                    3 -> weekSelect = weekSelect.plus(stringResource(R.string.addPage_diy_every))
                    2 -> for (code in 0..1) {
                        if (alarm.weekSelect and (0b1 shl code) != 0) {
                            weekSelect = weekSelect.plus(autoWeekName[code]).plus("  ")
                        }
                    }

                    1 -> for (code in 0..6) {
                        if (alarm.weekSelect and (0b1 shl code) != 0) {
                            weekSelect = weekSelect.plus(weekName[code]).plus("  ")
                        }
                    }

                    else -> weekSelect = weekSelect.plus(stringResource(R.string.addPage_diy_once))
                }

                Card(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                    onClick = {
                        val intent = Intent(context, AddActivity::class.java)
                        intent.putExtra("IS_EDIT", true)
                        intent.putExtra("ALARM_ID", alarm.id)
                        context?.startActivity(intent)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(
                                        "%02d:%02d",
                                        alarm.timeHour,
                                        alarm.timeMinute
                                    ),
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                                Text(
                                    text = alarm.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = weekSelect,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Switch(
                            checked = open,
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 8.dp,
                                    start = 0.dp
                                ),
                            onCheckedChange = {
                                alarm.isOpen = !alarm.isOpen
                                open = !open
                                context?.let {
                                    if (open) {
                                        setAlarm(alarm, it)
                                    } else {
                                        cancelAlarm(alarm, it)
                                    }
                                }
                                alarmViewModel.update(alarm)
                            }
                        )
                    }
                }
            }
        }
    }
}

//channelPage
@Composable
fun channelPage(settingsManager : SettingsManager){
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(32.dp)
    )
    {
        var currentMode by remember { mutableIntStateOf(settingsManager.getChanMode()) }
        var vibrationEnabled by remember { mutableStateOf(settingsManager.getChanVib()) }

        //current
        val selectText = when(settingsManager.getChanMode()){
            1 -> stringResource(R.string.page2_modeTitle_pm)
            2 -> stringResource(R.string.page2_modeTitle_head)
            3 -> stringResource(R.string.page2_modeTitle_sys)
            4 -> stringResource(R.string.page2_modeTitle_sm)
            else -> "Null"
        }
        Text(stringResource(R.string.page2_modeTitle_current, selectText))

        //vibrate
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = vibrationEnabled,
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 8.dp,
                        start = 0.dp
                    ),
                onCheckedChange = {
                    vibrationEnabled = !vibrationEnabled
                    settingsManager.saveChanVib(it)
                }
            )
            Text(stringResource(R.string.page2_vibrate))
        }

        //1
        Card(
            colors = updateChannelColor(1,currentMode),
            onClick = {
                currentMode = 1
                settingsManager.saveChanMode(1)
                updateChannelSelect(1)
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.page2_modeTitle_pm),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.page2_modeDescribe_pm),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        //2
        Card(
            colors = updateChannelColor(2,currentMode),
            onClick = {
                currentMode = 2
                settingsManager.saveChanMode(2)
                updateChannelSelect(2)
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.page2_modeTitle_head),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.page2_modeDescribe_head),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        //3
        Card(
            colors = updateChannelColor(3,currentMode),
            onClick = {
                currentMode = 3
                settingsManager.saveChanMode(3)
                updateChannelSelect(3)
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.page2_modeTitle_sys),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.page2_modeDescribe_sys),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        //4
        Card(
            colors = updateChannelColor(4,currentMode),
            onClick = {
                currentMode = 4
                settingsManager.saveChanMode(4)
                updateChannelSelect(4)
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.page2_modeTitle_sm),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.page2_modeDescribe_sm),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun updateChannelColor(self:Int,select:Int) : CardColors {
    if(self==select) {
        return CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)
    }
    return CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
}
fun updateChannelSelect(select:Int){

}


//preview in android studio.
