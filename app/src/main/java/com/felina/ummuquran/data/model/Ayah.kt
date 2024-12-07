package com.felina.ummuquran.data.model

data class Ayah (
    val surahName: String,
    val surahNameArabic: String,
    val surahNameArabicLong: String,
    val surahNameTranslation: String,
    val revelationPlace: String,
    val totalAyah: Long,
    val surahNo: Long,
    val english: List<String>,
    val arabic1: List<String>,
    val arabic2: List<String>
)
