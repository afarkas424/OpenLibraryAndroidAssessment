package com.example.openlibraryandroidassessment.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    val id: Int,
    val name: String,
    val count: Int
)
