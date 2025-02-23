package com.felina.ummuquran.ui.view.dashboard

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felina.ummuquran.data.local.Ramadan
import com.felina.ummuquran.data.local.RamadanDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class DashboardViewModel(private val repository: RamadanDao) : ViewModel() {

    private val _ramadan = MutableStateFlow<List<Ramadan>>(emptyList())
    val ramadan: StateFlow<List<Ramadan>> = _ramadan

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun fetchRamadan(dateNow: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _ramadan.value = repository.getDataRamadanByDate(dateNow)
            } catch (e: Exception) {
                Log.e("EEE",e.toString())
            } finally {
                _loading.value = false
            }
        }
    }
}