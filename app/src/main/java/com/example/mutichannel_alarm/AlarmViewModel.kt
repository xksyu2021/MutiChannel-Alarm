package com.example.mutichannel_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class AlarmViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




