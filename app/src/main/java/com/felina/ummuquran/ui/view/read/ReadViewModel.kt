package com.felina.ummuquran.ui.view.read


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felina.ummuquran.data.model.Ayah
import com.felina.ummuquran.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReadViewModel(private val repository: QuranRepository) : ViewModel() {

    private val _ayah = MutableStateFlow<Ayah?>(null)
    val ayah: StateFlow<Ayah?> = _ayah

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun fetchAyah(id: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _ayah.value = repository.getAyah(id)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _loading.value = false;
            }
        }
    }
}