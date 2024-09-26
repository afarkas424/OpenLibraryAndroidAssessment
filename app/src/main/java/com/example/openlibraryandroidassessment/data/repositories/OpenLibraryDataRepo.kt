package com.example.openlibraryandroidassessment.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.models.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request

class OpenLibraryDataRepo(private val bookDatabase: LibraryDatabaseHelper) {

    private val httpClient: OkHttpClient = OkHttpClient()
    private val jsonParser = Json { ignoreUnknownKeys = true }

    /**
     * Live data to post list of subjects to view model
     */
    private val _subjectList = MutableLiveData<List<Subject>>()
    val subjectList: LiveData<List<Subject>> get() = _subjectList

    /**
     * Live data to post list of books of selected subject to view model
     */
    private val _bookList = MutableLiveData<List<Book>>()
    val bookList: LiveData<List<Book>> get() = _bookList

    /**
     * Live data to post selected subject name to view model
     */
    private val _selectedSubjectTitle = MutableLiveData<String>()
    val selectedSubjectTitle: LiveData<String> get() = _selectedSubjectTitle

    /**
     * Live data to post book details screen information to view model
     */
    private val _bookDetails: MutableLiveData<BookDetailsScreenInformation> = MutableLiveData()
    val bookDetails: LiveData<BookDetailsScreenInformation> get() = _bookDetails

    /**
     * Fetches star wars book data from Open Library API and inserts it into the db.
     *
     * Queries DB for all subjects, and creates Subject structs based on aggregated information
     *
     * Posts a List of Subjects to the view model.
     */
    fun fetchSubjectGroupsAndInsertInLocalDatabase() {
        val books = fetchStarWarsBooksFromOpenLibraryAPI()
        bookDatabase.insertBooksAndSubjects(books)

        val subjectTitleToSubjectMap = bookDatabase.groupBooksBySubjectAndMapToSubjectStruct()
        _subjectList.postValue(subjectTitleToSubjectMap.values.toList())
    }

    /**
     * Returns all books in the db that contain subject of subject ID, and posts books and selected
     * subject title to the view model
     */
    fun getBooksForSelectedSubjectFromLocalDatabase(subjectID: Int) {
        val books = bookDatabase.getBooksBySubjectId(subjectID)
        _bookList.postValue(books)

        val selectedSubjectName = bookDatabase.getSubjectNameById(subjectID)
        _selectedSubjectTitle.postValue(selectedSubjectName ?: "Books")
    }

    /**
     * Calls helper functions to query OpenLibrary for book details, and posts the details to view model
     */
     fun retrieveAndPostBookDetailsScreenData(bookID: Int) {
        val selectedBook = bookDatabase.getBookById(bookID)
        val details = fetchBookDetailsFromOpenLibraryAPI(selectedBook.detailsKey)

        val detailsScreenInfo = BookDetailsScreenInformation(
            title = selectedBook.title ?: "Book not found",
            imgURL = "https://covers.openlibrary.org/b/id/${selectedBook.imageURLBase.plus("-L.jpg")}",
            description = details ?: "Description could not be found"
        )
        _bookDetails.postValue(detailsScreenInfo)
    }

    /**
     * Queries OpenLibrary API for book data at url "https://openlibrary.org$detailsKey.json" and parses response into
     * the work response class, which holds the description in the description sealed class
     */
    private fun fetchBookDetailsFromOpenLibraryAPI(detailsKey: String): String? {
        val url = "https://openlibrary.org$detailsKey.json"
        val request = Request.Builder().url(url).build()

        return executeRequest(request)?.let { jsonResponse ->
            val workResponse = jsonParser.decodeFromString<JsonElement>(jsonResponse).let {
                WorkResponse.fromJsonElement(it)
            }
            when (val desc = workResponse.description) {
                is Description.Simple -> desc.value
                is Description.Complex -> desc.value
                null -> null
            }
        }
    }

    /**
     * Queries OpenLibrary API for star wars books and processors results into a List of BookData
     */
    private fun fetchStarWarsBooksFromOpenLibraryAPI(): List<BookData> {
        val allBooks = mutableListOf<BookData>()
        var page = 1
        var hasMore = true

        while (hasMore) {
            val url = "https://openlibrary.org/search.json?q=subject:star%20wars&fields=key,title,author_key,author_name,first_publish_year,cover_i,subject&page=$page"
            val request = Request.Builder().url(url).build()

            executeRequest(request)?.let { jsonResponse ->
                val bookQueryResponse = jsonParser.decodeFromString<BookQueryResponse>(jsonResponse)
                val responseBooks = bookQueryResponse.docs ?: emptyList()

                if (responseBooks.isEmpty()) {
                    hasMore = false
                } else {
                    allBooks.addAll(responseBooks)
                    page++
                }
            } ?: run {
                hasMore = false // Stop if the request fails
            }
        }
        return allBooks
    }

    /**
     * Executes provided request
     */
    private fun executeRequest(request: Request): String? {
        return try {
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string().also {
                        println("Response JSON: $it") // Debug log
                    }
                } else {
                    println("Failed response: ${response.code} - ${response.message}")
                    null
                }
            }
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
            null
        }
    }
}
