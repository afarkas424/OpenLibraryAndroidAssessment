package com.example.openlibraryandroidassessment.data.models

/**
 * Data class to hold information to be displayed on the Book Screen
 */
data class Book(
    val id: Int,
    val title: String,
    val publishedYear: Int,
    val imageURLBase: String,
    val authors: String,
    val detailsKey: String
)
