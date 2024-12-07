package com.felina.ummuquran.data.repository

import com.felina.ummuquran.data.network.ApiService
import com.felina.ummuquran.data.network.ApiService2

class QuranRepository(private val apiService: ApiService,private val apiService2: ApiService2) {
    suspend fun getSurah() = apiService.getSurah()
    suspend fun getAyah(id: String) = apiService.getAyah(id)
    suspend fun getHadith(id: String) = apiService2.getHadith(id)
}