package com.example.openlibraryandroidassessment.data.repositories

import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.models.BookData
import com.example.openlibraryandroidassessment.data.models.BookQueryResponse
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

internal class BookDataRepo(private val bookDatabase: LibraryDatabaseHelper) {

    fun fetchBooksAndCreateDatabase() {
        val books = fetchStarWarsBooksJson()

        bookDatabase.insertBooksAndSubjects(books)
        val subjectsBookMap = bookDatabase.getBooksGroupedBySubject()

        // TODO: post map to view model


    }


    private fun fetchStarWarsBooksJson(): List<BookData> {
        val client = OkHttpClient()
        val allBooks = mutableListOf<BookData>()
        var page = 1
        var hasMore = true

        while (hasMore) {
            val url =
                "https://openlibrary.org/search.json?q=subject:star%20wars&fields=key,title,author_key,author_name,first_publish_year,cover_i,subject&page=$page"

            val request = Request.Builder()
                .url(url)
                .build()

            val response: Response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                println("Error: ${response.code}")
                break
            }

            val jsonResponse = response.body?.string()
            jsonResponse?.let {
                val json = Json { ignoreUnknownKeys = true }
                val bookQueryResponse = json.decodeFromString<BookQueryResponse>(it)
                val responseBooks = bookQueryResponse.docs

                if (responseBooks?.isEmpty() == true) {
                    hasMore = false // No more books, stop fetching
                } else {
                    if (responseBooks != null) {
                        allBooks.addAll(responseBooks)
                    } // Add the current page's books to the list
                    page++ // Increment the page number for the next request
                }
            } ?: run {
                println("Error parsing JSON response.")
                hasMore = false // Stop if JSON is null
            }
        }

        return allBooks
    }


}
