package com.xksyu.mutichannel_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(private val id: Int = 0 , private val repository: AlarmRepository) : ViewModel() {
    fun insert(data :AlarmData) {
        viewModelScope.launch {
            repository.insertAlarm(data)
        }
    }
    fun delete(data :AlarmData? = null) {
        viewModelScope.launch {
            repository.deleteAlarm(data)
        }
    }
    fun update(data :AlarmData?) {
        viewModelScope.launch {
            repository.updateAlarm(data)
        }
    }

    val alarms: StateFlow<List<AlarmData>> = repository.alarms
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _alarm = MutableStateFlow<AlarmData?>(null)
    val alarmById: StateFlow<AlarmData?> = _alarm
    init {
        loadAlarm()
    }
    private fun loadAlarm() {
        viewModelScope.launch {
            val result = repository.getById(id)
            _alarm.value = result
        }
    }
}

class AlarmViewModelFactory(private val id:Int = 0,private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(id,repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




