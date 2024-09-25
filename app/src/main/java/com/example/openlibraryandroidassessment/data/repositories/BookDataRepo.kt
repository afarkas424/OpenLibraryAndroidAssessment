package com.example.openlibraryandroidassessment.data.repositories

import android.util.EventLogTags
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.models.Book
import com.example.openlibraryandroidassessment.data.models.BookData
import com.example.openlibraryandroidassessment.data.models.BookDetailsScreenInformation
import com.example.openlibraryandroidassessment.data.models.BookQueryResponse
import com.example.openlibraryandroidassessment.data.models.Description
import com.example.openlibraryandroidassessment.data.models.Subject
import com.example.openlibraryandroidassessment.data.models.WorkResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class BookDataRepo(private val bookDatabase: LibraryDatabaseHelper) {

    private val _subjectList = MutableLiveData<List<Subject>>()
    val subjectList: LiveData<List<Subject>> get() = _subjectList

    private val _bookList = MutableLiveData<List<Book>>()
    val bookList: LiveData<List<Book>> get() = _bookList

    private val _selectedSubjectTitle = MutableLiveData<String>()
    val selectedSubjectTitle: LiveData<String> get() = _selectedSubjectTitle

    private val _bookDetails: MutableLiveData<BookDetailsScreenInformation> = MutableLiveData<BookDetailsScreenInformation>()
    val bookDetails: LiveData<BookDetailsScreenInformation> get() = _bookDetails

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

        val selectedSubjectName = bookDatabase.getSubjectNameById(subjectID)
        // also post the name of the selected subject
        _selectedSubjectTitle.postValue(selectedSubjectName ?: "Books")
    }

    suspend fun retrieveAndPostBookDetailsScreenData(bookID: Int) {
        // need book title, book image, and to fetch details
        val selectedBook = bookDatabase.getBookById(bookID)


        val details = fetchBookDetailsFromOpenLibraryAPI(selectedBook.detailsKey)
        // construct and post book details
        val detailsScreenInfo = BookDetailsScreenInformation(
            title = selectedBook.title,
            imgURL = "https://covers.openlibrary.org/b/id/${selectedBook.imageURLBase.plus("-L.jpg")}",
            description = details ?: "Description could not be found"
        )
        _bookDetails.postValue(detailsScreenInfo)

    }

    private fun fetchBookDetailsFromOpenLibraryAPI(detailsKey: String): String? {
        val client = OkHttpClient()
        val url = "https://openlibrary.org$detailsKey.json"
        val request = Request.Builder()
            .url(url)
            .build()
        val json = Json { ignoreUnknownKeys = true }

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    if (jsonResponse != null) {
                        // Log the response for debugging
                        println("Response JSON: $jsonResponse")

                        // Use the companion object method to handle variability
                        val workResponse = json.decodeFromString<JsonElement>(jsonResponse).let {
                            WorkResponse.fromJsonElement(it)
                        }

                        when (val desc = workResponse.description) {
                            is Description.Simple -> desc.value
                            is Description.Complex -> desc.value // Adjust according to your needs
                            null -> null
                        }
                    } else {
                        // Handle null response body
                        null
                    }
                } else {
                    // Log the unsuccessful response
                    println("Failed response: ${response.code} - ${response.message}")
                    null
                }
            }
        } catch (e: Exception) {
            // Log the exception
            println("Error fetching book details: ${e.message}")
            null
        }
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
