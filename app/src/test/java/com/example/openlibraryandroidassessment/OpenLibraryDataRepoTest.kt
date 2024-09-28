package com.example.openlibraryandroidassessment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.models.*
import com.example.openlibraryandroidassessment.data.repositories.OpenLibraryDataRepo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


class OpenLibraryDataRepoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var openLibraryDataRepo: OpenLibraryDataRepo

    @Mock
    private lateinit var mockDatabase: LibraryDatabaseHelper

    @Mock
    private lateinit var selectedSubjectObserver: Observer<String>

    @Mock
    private lateinit var bookDetailsObserver: Observer<BookDetailsScreenInformation>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        openLibraryDataRepo = OpenLibraryDataRepo(mockDatabase)
        openLibraryDataRepo.selectedSubjectTitle.observeForever(selectedSubjectObserver)
        openLibraryDataRepo.bookDetails.observeForever(bookDetailsObserver)

    }

    @Test
    fun testGetBooksForSelectedSubjectFromLocalDatabase() {
        val subjectID = 1
        val mockBooks = listOf(Book(1, "key1", 2000, imageURLBase = "img", authors = "Fitzgerald", detailsKey = "daisy"))
        val subjectName = "Subject1"

        // Mock the database responses
        `when`(mockDatabase.getBooksBySubjectId(subjectID)).thenReturn(mockBooks)
        `when`(mockDatabase.getSubjectNameById(subjectID)).thenReturn(subjectName)

        openLibraryDataRepo.getBooksForSelectedSubjectFromLocalDatabase(subjectID)

        // LiveData updated correctly
        verify(selectedSubjectObserver).onChanged(subjectName)
    }

    @Test
    fun testRetrieveAndPostBookDetailsScreenData() {
        val bookID = 1
        val selectedBook = Book(1, "key1", 2000, imageURLBase = "img", authors = "Fitzgerald", detailsKey = "daisy")

        // Mock the database call
        `when`(mockDatabase.getBookById(bookID)).thenReturn(selectedBook)

        // Call the method under test
        openLibraryDataRepo.retrieveAndPostBookDetailsScreenData(bookID)

        // verify expected details returned
        val expectedDetails = BookDetailsScreenInformation(
            title = "key1",
            imgURL = "https://covers.openlibrary.org/b/id/img-L.jpg",
            description = "Description could not be found" // Assuming it returns null
        )
        verify(bookDetailsObserver).onChanged(expectedDetails)
    }
}
