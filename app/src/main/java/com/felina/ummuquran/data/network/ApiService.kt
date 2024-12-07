package com.felina.ummuquran.data.network

import com.felina.ummuquran.data.model.Ayah
import com.felina.ummuquran.data.model.Hadith
import com.felina.ummuquran.data.model.Surah
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("surah.json")
    suspend fun getSurah(): List<Surah>
    @GET("{id}.json")
    suspend fun getAyah(
        @Path("id") id: String,
    ): Ayah
}
interface ApiService2 {
    @GET("{id}/")
    suspend fun getHadith(
        @Path("id") id: String,
    ): Hadith
}

object ApiClient {
    private const val BASE_URL = "https://quranapi.pages.dev/api/"
    private const val BASE_URL_2 = "https://random-hadith-generator.vercel.app/"

    val retrofit: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    val retrofit2: ApiService2 by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_2)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService2::class.java)
    }
}