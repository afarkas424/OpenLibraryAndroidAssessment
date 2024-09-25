package com.example.openlibraryandroidassessment.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.models.Book
import com.example.openlibraryandroidassessment.data.models.BookData
import com.example.openlibraryandroidassessment.data.models.BookQueryResponse
import com.example.openlibraryandroidassessment.data.models.Subject
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class BookDataRepo(private val bookDatabase: LibraryDatabaseHelper) {

    private val _subjectList = MutableLiveData<List<Subject>>()
    val subjectList: LiveData<List<Subject>> get() = _subjectList

    private val _bookList = MutableLiveData<List<Book>>()
    val bookList: LiveData<List<Book>> get() = _bookList

    private val _bookDetails = MutableLiveData<String>()
    val bookDetails: LiveData<String> get() = _bookDetails

    /**
     * Fetches book and subject data, inserts it to SQLite database.
     * Posts fetched subjects to libraryViewModel.
     */
     fun fetchSubjectGroupsAndInsertInLocalDatabase() {
        val books = fetchStarWarsBooksFromOpenLibraryAPI()
        bookDatabase.insertBooksAndSubjects(books)

        val subjectTitleToSubjectMap = bookDatabase.groupBooksBySubjectAndMapToSubjectStruct()
        // post subjects to the main thread
        _subjectList.postValue(subjectTitleToSubjectMap.values.toList())
    }

    /**
     * Gets books that contain the selected subject of id subjectID from the local db.
     * Posts fetched books to libraryViewModel.
     */
    fun getBooksForSelectedSubjectFromLocalDatabase(subjectID: Int) {
        val books = bookDatabase.getBooksBySubjectId(subjectID)
        _bookList.postValue(books)
    }

    /**
     * Searches open library for all Star Wars books. Iterates through paginated responss and parses
     * into a BookData struct.
     *
     * @return allBooks, a list of BookData for books relating to star wars
     */
    private fun fetchStarWarsBooksFromOpenLibraryAPI(): List<BookData> {
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
