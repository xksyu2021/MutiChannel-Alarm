package com.example.mutichannel_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    fun getAllId() {
        viewModelScope.launch {
            repository.getAllIds()
        }
    }fun getById(id :Long) {
        viewModelScope.launch {
            repository.getById(id)
        }
    }
}




