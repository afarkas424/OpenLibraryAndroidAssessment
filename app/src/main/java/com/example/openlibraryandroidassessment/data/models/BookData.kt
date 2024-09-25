package com.example.openlibraryandroidassessment.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serializable data class for parsing in Book information from OpenLibrary API responses
 */
@Serializable
data class BookData(
    val author_key: List<String> = emptyList(),
    val author_name: List<String> = emptyList(),
    val cover_i: Int? = null,
    val first_publish_year: Int? = null,
    val key: String? = null,
    val title: String? = null,
    val subject: List<String> = emptyList()
)

/**
 * Serializable data class for handling pagination in parsing OpenLibrary API query responses
 */
@Serializable
data class BookQueryResponse(
    @SerialName("numFound") val numFound: Int?,
    @SerialName("start") val start: Int?,
    @SerialName("docs") val docs: List<BookData>?,
    @SerialName("numFoundExact") val numFoundExact: Boolean?
)

