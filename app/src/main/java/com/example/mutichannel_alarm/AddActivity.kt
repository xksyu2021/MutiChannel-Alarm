package com.example.mutichannel_alarm

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme
import java.util.Calendar

class AddActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isEdit = intent.getBooleanExtra("IS_EDIT", false)
        val editID = intent.getIntExtra("ALARM_ID", -1)

        val alarmViewModel: AlarmViewModel by viewModels {
            val repository = (application as MCApplication).repository
            AlarmViewModelFactory(editID,repository)
        }

        val tempDB =  AlarmTemp()
        setContent {
            ContrastAwareReplyTheme{
                AddPage(onBack = {
                        finish()
                        println("----------------EXIT without save----------------")
                    },
                    onSave = {
                        if(isEdit){
                            if(onSaveEdit(tempDB,alarmViewModel,this)) { finish() }
                        }else{
                            if(onSave(tempDB,alarmViewModel,this)) { finish() }
                        }
                    },
                    isEdit = isEdit,
                    temp = tempDB,
                    alarmViewModel = alarmViewModel,
                )
                if(isEdit){
                    println("----------------EDIT mode----------------")
                }else{
                    println("----------------CREATE mode----------------")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPage(onBack: () -> Unit = {}, onSave: () -> Unit = {}, isEdit : Boolean = false, temp :AlarmTemp,alarmViewModel: AlarmViewModel){
    var showCheck by remember { mutableStateOf(false) }
    var showDelCheck by remember { mutableStateOf(false) }

    val alarmById by alarmViewModel.alarmById.collectAsState()

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
                        alarmViewModel.delete(alarmViewModel.alarmById.value)
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
            addConfigList(temp = temp,isEdit = isEdit, alarmById = alarmById)
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
fun addConfigList(temp :AlarmTemp,isEdit : Boolean,alarmById: AlarmData?){
    val weekName = arrayOf("Mon","Tue","Wen","Tur","Fri","Sat","Sun")
    val autoWeekName = arrayOf("weekdays","weekends")

    println("****** List Recompose Count ******")
    var updateStatu by remember { mutableStateOf(!isEdit) }
    if (isEdit) {
        LaunchedEffect(alarmById) {
            alarmById?.let { alarm ->
                temp.hour.value = alarm.timeHour
                temp.minute.value = alarm.timeMinute
                temp.text.value = alarm.name
                temp.autoEnabled.value = alarm.autoWeek
                temp.remindTimes.value = alarm.remindTime
                temp.remindMinutes.value = alarm.remindMinute
                temp.remindEnabled.value = alarm.remind
                if(temp.autoEnabled.value){
                    for(code in 0..1){
                        if((alarm.weekSelect and (0b1 shl code))!=0) temp.autoDays[code] = true
                    }
                }else{
                    for(code in 0..6){
                        if((alarm.weekSelect and (0b1 shl code))!=0) temp.days[code] = true
                    }
                }
                updateStatu = true
                println("****** LaunchedEffect for temp Count ******")
                println("DEBUG AlarmData values:")
                println("  id: ${alarm.id}")
                println("  timeHour: ${alarm.timeHour}, timeMinute: ${alarm.timeMinute}")
                println("  name: ${alarm.name}")
                println("DEBUG AlarmTemp values:")
                println("  hour: ${temp.hour.value}, minute: ${temp.minute.value}")
                println("  text: ${temp.text.value}")
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(horizontal = 10.dp)
            .padding(vertical = 15.dp)
    ){
        TextField(
            singleLine = true,
            value = temp.text.value,
            onValueChange = { temp.text.value = it },
            label = { Text("Alarm Name") },
            placeholder = null,
            modifier = Modifier
                .padding(10.dp)
        )
    }
    if(updateStatu){
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(horizontal = 10.dp)
                .padding(vertical = 15.dp)
        ){
            val currentTime = Calendar.getInstance()
            val defHour = if(isEdit) temp.hour.value else currentTime.get(Calendar.HOUR_OF_DAY)
            val defMinute = if(isEdit) temp.minute.value else currentTime.get(Calendar.MINUTE)

            val timePickerState = rememberTimePickerState(
                initialHour = defHour,
                initialMinute = defMinute,
                is24Hour = true
            )

            println("DEBUG TimeInput")
            println("   initHour : ${timePickerState.hour}, initMinute : ${timePickerState.minute}")
            if(isEdit) {
                LaunchedEffect(temp.hour.value, temp.minute.value) {
                    timePickerState.hour = temp.hour.value
                    timePickerState.minute = temp.minute.value
                }
                println("****** LaunchedEffect for timeInput Count ******")
                println("DEBUG TimeInput in LaunchedEffect")
                println("   initHour : ${timePickerState.hour}, initMinute : ${timePickerState.minute}")
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            ){
                TimeInput(
                    state = timePickerState,
                )
                temp.hourGet.value = timePickerState.hour
                temp.minuteGet.value = timePickerState.minute
            }
        }
        val focusManager = LocalFocusManager.current
        LaunchedEffect(Unit) {
            focusManager.clearFocus()
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
                checked = temp.autoEnabled.value,
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 8.dp,
                        start = 0.dp
                    ),
                onCheckedChange = {
                    temp.autoEnabled.value = !temp.autoEnabled.value
                }
            )
            Text(stringResource(R.string.addPage_autoWeek_text))
        }
    }
    if (temp.autoEnabled.value){
        Row {
            autoDaysChip(0,temp.autoDays,autoWeekName)
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            autoDaysChip(1,temp.autoDays,autoWeekName)
        }
    }else{
        Row {
            daysChip(0,temp.days,weekName)
            for (code in 1..2){
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                daysChip(code,temp.days,weekName)
            }
        }
        Row {
            daysChip(3,temp.days,weekName)
            for (code in 4..6){
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                daysChip(code,temp.days,weekName)
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
                Text(temp.ringtone.value)
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
                checked = temp.remindEnabled.value,
                modifier = Modifier
                    .padding(10.dp),
                onCheckedChange = {
                    temp.remindEnabled.value = !temp.remindEnabled.value
                }
            )
        }
        if(temp.remindEnabled.value) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${temp.remindTimes.value}")
                remindTimeDropdown(temp)
                Text(stringResource(R.string.addPage_diy_remind_1),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Text("${temp.remindMinutes.value}")
                remindMinutesDropdown(temp)
                Text(stringResource(R.string.addPage_diy_remind_2),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun remindTimeDropdown(temp :AlarmTemp) {
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
                    onClick = {
                        temp.remindTimes.value = code
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun remindMinutesDropdown(temp :AlarmTemp) {
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
                    onClick = {
                        temp.remindMinutes.value = code
                        expanded = false
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
fun onSave(temp :AlarmTemp, alarmViewModel: AlarmViewModel, context: Context) : Boolean {
    var weekSelectTemp = 0b0
    if (temp.autoEnabled.value) {
        for (code in 0..1) {
            if(temp.autoDays[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
    } else {
        for (code in 0..6) {
            if(temp.days[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
    }
    if(weekSelectTemp==0){
        Toast.makeText(context, "no day selected", Toast.LENGTH_LONG).show()
        return false
    }
    val db = AlarmData(
        name = temp.text.value,
        timeHour = temp.hourGet.value,
        timeMinute = temp.minuteGet.value,
        autoWeek = temp.autoEnabled.value,
        remind = temp.remindEnabled.value,
        remindTime = temp.remindTimes.value,
        remindMinute = temp.remindMinutes.value,
        weekSelect = weekSelectTemp
    )
    alarmViewModel.insert(db)
    alarmViewModel.update(db)
    setAlarm(db,context)
    println("----------------SAVE----------------")
    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
    return true
}
@OptIn(ExperimentalMaterial3Api::class)
fun onSaveEdit(temp :AlarmTemp, alarmViewModel: AlarmViewModel, context: Context) : Boolean {
    var weekSelectTemp = 0b0
    if (temp.autoEnabled.value) {
        for (code in 0..1) {
            if(temp.autoDays[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
    } else {
        for (code in 0..6) {
            if(temp.days[code]){
                weekSelectTemp = weekSelectTemp or (0b1 shl code)
            }
        }
    }
    if(weekSelectTemp==0){
        Toast.makeText(context, "no day selected", Toast.LENGTH_LONG).show()
        return false
    }
    alarmViewModel.alarmById.value?.let { alarm ->
        with(alarm) {
            name = temp.text.value
            timeHour = temp.hourGet.value
            timeMinute = temp.minuteGet.value
            autoWeek = temp.autoEnabled.value
            remindTime = temp.remindTimes.value
            remindMinute = temp.remindMinutes.value
            remind = temp.remindEnabled.value
            weekSelect = weekSelectTemp
        }
    }
    alarmViewModel.update(alarmViewModel.alarmById.value)
    println("----------------SAVE----------------")
    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
    return true
}