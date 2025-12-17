package com.example.mutichannel_alarm

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import java.util.Calendar
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.room.Database
import androidx.room.PrimaryKey
import androidx.room.Room
import kotlinx.coroutines.Dispatchers


class AddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isEdit = intent.getBooleanExtra("IS_EDIT", false)
        setContent {
            ContrastAwareReplyTheme{
                AddPage(onBack = {
                        finish()
                    },
                    onSave = {
                        if(isEdit){
                            onSaveEdit()
                        }else {
                            onSave()
                        }
                        finish()
                    },
                    isEdit = isEdit,
                    context = this
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPage(onBack: () -> Unit = {}, onSave: () -> Unit = {}, isEdit : Boolean = false, context: Context? = null){
    var showCheck by remember { mutableStateOf(false) }
    var showDelCheck by remember { mutableStateOf(false) }
    BackHandler(enabled = true) {
        showCheck = true
    }
    if (showCheck) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.pageAdd_check_title)) },
            text = {
                if(isEdit){
                    Text(stringResource(R.string.pageAdd_check_edit))
                }else {
                    Text(stringResource(R.string.pageAdd_check_new))
                }
            },
            confirmButton = {
                Button(onClick = { showCheck = false }) {
                    Text(stringResource(R.string.pageAdd_check_stay))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                        showCheck = false
                        onBack()
                    }
                ) {
                    Text(stringResource(R.string.pageAdd_check_back))
                }
            }
        )
    }
    if (showDelCheck) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Delete") },
            text = {
                Text(stringResource(R.string.pageAdd_delCheck_text))
            },
            confirmButton = {
                OutlinedButton(onClick = { showDelCheck = false }) {
                    Text(stringResource(R.string.pageAdd_check_stay))
                }
            },
            dismissButton = {
                Button(onClick = {
                        showDelCheck = false
                        onBack()
                    }
                ) {
                    Text(stringResource(R.string.pageAdd_delCheck_delete))
                }
            }
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    if(isEdit){
                        Text(
                            text = stringResource(R.string.title_edit),
                            modifier = Modifier.fillMaxWidth(0.95f),
                            textAlign = TextAlign.Center
                        )
                    }else {
                        Text(
                            text = stringResource(R.string.title_add),
                            modifier = Modifier.fillMaxWidth(0.95f),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                            showCheck = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.backHead)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.confirmHead)
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 25.dp)
        ) {
            addConfigList()
            val focusManager = LocalFocusManager.current
            LaunchedEffect(Unit) {
                focusManager.clearFocus()
            }
            if (isEdit){
                Button(
                    onClick = {showDelCheck = true}
                ) {
                    Text(stringResource(R.string.pageAdd_delCheck_title))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addConfigList(){
    var text by remember { mutableStateOf("default") }
    val weekName = arrayOf("Mon","Tue","Wen","Tur","Fri","Sat","Sun")
    val autoWeekName = arrayOf("weekdays","weekends")
    var autoEnabled by remember { mutableStateOf(false) }
    var autoDays = remember { mutableStateListOf(true, false) }
    var days = remember { mutableStateListOf<Boolean>() }
        .apply { repeat(7) { add(false) } }
    var remindTimes = remember { mutableStateOf(3) }
    var remindMinutes = remember { mutableStateOf(5) }
    var remindEnabled by remember { mutableStateOf(true) }
    var ringtone by remember { mutableStateOf("default") }
    var hour by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(horizontal = 10.dp)
            .padding(vertical = 15.dp)
    ){
        TextField(
            singleLine = true,
            value = text,
            onValueChange = { text = it },
            label = { Text("Alarm Name") },
            placeholder = null,
            modifier = Modifier
                .padding(10.dp)

        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(horizontal = 10.dp)
            .padding(vertical = 15.dp)
    ){
        val currentTime = Calendar.getInstance()
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = true,
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ){
            TimeInput(
                state = timePickerState,
            )
            hour = timePickerState.hour
            minute = timePickerState.minute
        }
    }

    HorizontalDivider(
        thickness = 2.dp,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(vertical = 15.dp)
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(horizontal = 10.dp)
            .padding(vertical = 15.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = autoEnabled,
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 8.dp,
                        start = 0.dp
                    ),
                onCheckedChange = {
                    autoEnabled = !autoEnabled
                }
            )
            Text(stringResource(R.string.addPage_autoWeek_text))
        }
    }
    if (autoEnabled){
        Row {
            autoDaysChip(0,autoDays,autoWeekName)
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            autoDaysChip(1,autoDays,autoWeekName)
        }
    }else{
        Row {
            daysChip(0,days,weekName)
            for (code in 1..2){
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                daysChip(code,days,weekName)
            }
        }
        Row {
            daysChip(3,days,weekName)
            for (code in 4..6){
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                daysChip(code,days,weekName)
            }
        }
    }

    HorizontalDivider(
        thickness = 2.dp,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(vertical = 15.dp)
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = { },
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(horizontal = 5.dp)
            .padding(top = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                Text(stringResource(R.string.addPage_diy_ringtone),
                    style = MaterialTheme.typography.titleLarge
                )
                Text("$ringtone")
            }
        }
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(horizontal = 5.dp)
            .padding(vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(stringResource(R.string.addPage_diy_remind),
                style = MaterialTheme.typography.titleLarge
            )
            Switch(
                checked = remindEnabled,
                modifier = Modifier
                    .padding(10.dp),
                onCheckedChange = {
                    remindEnabled = !remindEnabled
                }
            )
        }
        if(remindEnabled) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${remindTimes.value}")
                remindTimeDropdown(remindTimes)
                Text(stringResource(R.string.addPage_diy_remind_1),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Text("${remindMinutes.value}")
                remindMinutesDropdown(remindMinutes)
                Text(stringResource(R.string.addPage_diy_remind_2),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun remindTimeDropdown(remindTimes: MutableState<Int>) {
    var expanded by remember { mutableStateOf(false) }
    Box{
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (code in 1..5){
                DropdownMenuItem(
                    text = { Text("$code") },
                    onClick = { remindTimes.value = code }
                )
            }
        }
    }
}
@Composable
fun remindMinutesDropdown(remindMinutes: MutableState<Int>) {
    var expanded by remember { mutableStateOf(false) }
    Box{
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (code in listOf(3,5,10,15,20)){
                DropdownMenuItem(
                    text = { Text("$code") },
                    onClick = { remindMinutes.value = code }
                )
            }
        }
    }
}

@Composable
fun autoDaysChip(code :Int,autoDays :SnapshotStateList<Boolean>,autoWeekName :Array<String>) {
    FilterChip(
        onClick = { autoDays[code] = !autoDays[code] },
        label = {
            Text(autoWeekName[code])
        },
        selected = true,
        leadingIcon = if (autoDays[code]) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
    )
}
@Composable
fun daysChip(code :Int,days :SnapshotStateList<Boolean>,weekName :Array<String>){
    FilterChip(
        onClick = { days[code] = !days[code]  },
        label = {
            Text(weekName[code])
        },
        selected = true,
        leadingIcon = if (days[code]) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                )
            }
        } else {
            null
        }
    )
}

fun onSave(){

}

fun onSaveEdit(){

}

@Preview
@Composable
fun AddPreview(){
    ContrastAwareReplyTheme{
        AddPage(isEdit = false)
    }
}