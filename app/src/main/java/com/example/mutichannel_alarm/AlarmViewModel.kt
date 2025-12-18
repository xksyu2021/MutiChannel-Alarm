package com.example.mutichannel_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {
    fun insert(data :AlarmData) {
        viewModelScope.launch {
            repository.insertAlarm(data)
        }
    }
    fun delete(data :AlarmData) {
        viewModelScope.launch {
            repository.deleteAlarm(data)
        }
    }
    fun update(data :AlarmData) {
        viewModelScope.launch {
            repository.updateAlarm(data)
        }
    }

    fun getById(id :Long) : StateFlow<AlarmData> {
        return repository.getById(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AlarmData()
            )
    }
    val alarms: StateFlow<List<AlarmData>> = repository.alarms
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

class AlarmViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




