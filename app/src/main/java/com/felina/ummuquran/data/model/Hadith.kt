package com.felina.ummuquran.data.model

data class Hadith (
    val data: Data
)

data class Data (
    val book: String,
    val book_name: String,
    val chapter_name: String,
    val hadith_english: String,
    val header: String,
    val id: Long,
    val refno: String
)