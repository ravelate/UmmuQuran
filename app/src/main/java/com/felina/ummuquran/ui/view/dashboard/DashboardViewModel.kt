package com.felina.ummuquran.ui.view.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felina.ummuquran.data.local.Ramadan
import com.felina.ummuquran.data.local.RamadanDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: RamadanDao) : ViewModel() {

    private val _ramadan = MutableStateFlow<List<Ramadan>>(emptyList())
    val ramadan: StateFlow<List<Ramadan>> = _ramadan

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun fetchRamadan(dateNow: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _ramadan.value = repository.getDataRamadanByDate(dateNow).sortedBy { it.isDone }
            } catch (e: Exception) {
                Log.e("EEE",e.toString())
            } finally {
                _loading.value = false
            }
        }
    }
    fun fetchUpdateRamadanIsDone(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.updateRamadanTask(id);
            } catch (e: Exception) {
                Log.e("EEE",e.toString())
            } finally {
                _loading.value = false
            }
        }
    }
    fun insertRamadan(title: String, date: String, startTime: String, priority: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.insertRamadan(Ramadan(
                    title = title,
                    date = date,
                    startTime = startTime,
                    priorityLevel = priority,
                    isDone = false
                ));
            } catch (e: Exception) {
                Log.e("EEE",e.toString())
            } finally {
                _loading.value = false
            }
        }
    }
}