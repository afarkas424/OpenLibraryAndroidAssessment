package com.example.openlibraryandroidassessment.data.models

data class Book(
    val id: Int,
    val title: String,
    val publishingInfo: String,
    val subjectId: Int,
    val imageUrl: String
)
