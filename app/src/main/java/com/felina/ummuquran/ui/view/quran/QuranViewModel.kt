package com.felina.ummuquran.ui.view.quran

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felina.ummuquran.data.model.Hadith
import com.felina.ummuquran.data.model.Surah
import com.felina.ummuquran.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuranViewModel(private val repository: QuranRepository) : ViewModel() {

    private val _surah = MutableStateFlow<List<Surah>>(emptyList())
    val surah: StateFlow<List<Surah>> = _surah

    private val _hadith = MutableStateFlow<Hadith?>(null)
    val hadith: StateFlow<Hadith?> = _hadith

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun fetchSurah() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _hadith.value = repository.getHadith(listOf("bukhari", "muslim", "abudawud", "ibnmajah", "tirmidhi").random())
                _surah.value = repository.getSurah()
            } catch (e: Exception) {
                Log.e("EEE",e.toString())
            } finally {
                _loading.value = false
            }
        }
    }
}