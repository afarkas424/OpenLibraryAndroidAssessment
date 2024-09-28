package com.example.openlibraryandroidassessment.database.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.models.BookData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LibraryDatabaseHelperTest {

    private lateinit var dbHelper: LibraryDatabaseHelper

    private val books = listOf(
        BookData(
            cover_i = 1,
            subject = listOf("Fantasy"),
            author_key = listOf("1"),
            author_name = listOf("J.R.R. Tolkien"),
            first_publish_year = 2000,
            key = "works/12345",
            title = "The Lord of the Rings"
        ),
        BookData(
            cover_i = 2,
            subject = listOf("Fantasy"),
            author_key = listOf("2"),
            author_name = listOf("J.R.R. Tolkien"),
            first_publish_year = 2000,
            key = "works/12345",
            title = "The Lord of the Flies"
        ),
        BookData(
            cover_i = 2,
            subject = listOf("Mystery"),
            author_key = listOf("2"),
            author_name = listOf("J.R.R. Tolkien"),
            first_publish_year = 2000,
            key = "works/12345",
            title = "The Lord of the Flies 2"
        ),
        BookData(
            cover_i = 2,
            subject = listOf("Mystery"),
            author_key = listOf("2"),
            author_name = listOf("J.R.R. Tolkien"),
            first_publish_year = 2000,
            key = "works/12345",
            title = "The Lord of the Flies 3"
        ),
        BookData(
            cover_i = 2,
            subject = listOf("Biography"),
            author_key = listOf("2"),
            author_name = listOf("J.R.R. Tolkien"),
            first_publish_year = 2000,
            key = "works/12345",
            title = "The Lord of the Flies 3"
            )
        )

    @Before
    fun setUp() {
        // Use an in-memory database for testing
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = LibraryDatabaseHelper(context)
        // Clear DB before test
        dbHelper.wipeDataBase()
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    /**
     * Tests db insertion, subjects creation, and that upon group by subject, only subjects with count > 1
     * are returned.
     */
    @Test
    fun testInsertBooksAndSubjectsAndSubjectGrouping() {
        // add books and subjects to db
        dbHelper.insertBooksAndSubjects(books)

        val subjects = dbHelper.groupBooksBySubjectAndCreateSubjects()
        // ensures that only subjects with a count of books > 1 are returned by grouping
        assertEquals(2, subjects.size)
        assertEquals("Fantasy", subjects[0].name)
        assertEquals("Mystery", subjects[1].name)

        val fantasyBooksInDb = dbHelper.getBooksBySubjectId(subjects[0].id)
        val mysteryBooksInDb = dbHelper.getBooksBySubjectId(subjects[1].id)
        assertEquals(2, fantasyBooksInDb.size)
        assertEquals(2, mysteryBooksInDb.size)
    }

    /**
     * Tests db query for books that contain a subject of ID subject ID returns correct results
     */
    @Test
    fun testGetBooksBySubjectId() {
        dbHelper.insertBooksAndSubjects(books)

        val subjects = dbHelper.groupBooksBySubjectAndCreateSubjects()
        val fantasyBooks = dbHelper.getBooksBySubjectId(subjects[0].id)

        // ensures only 2 fantasy books were found in the DB
        assertEquals(2, fantasyBooks.size)
        assertEquals("The Lord of the Rings", fantasyBooks[0].title)
        assertEquals("The Lord of the Flies", fantasyBooks[1].title)
    }

    /**
     * Tests db read on subject by ID returns desired Subject
     */
    @Test
    fun testGetSubjectNameById() {
        dbHelper.insertBooksAndSubjects(books)

        val subjects = dbHelper.groupBooksBySubjectAndCreateSubjects()
        val subjectName = dbHelper.getSubjectNameById(subjects[0].id)
        val secondSubjectName = dbHelper.getSubjectNameById(subjects[1].id)

        assertNotNull(subjectName)
        assertEquals("Fantasy", subjectName)
        assertNotNull(secondSubjectName)
        assertEquals("Mystery", secondSubjectName)
    }

    /**
     * Tests db read on book by ID returns desired book
     */
    @Test
    fun testGetBookById() {
        dbHelper.insertBooksAndSubjects(books)

        val subjects = dbHelper.groupBooksBySubjectAndCreateSubjects()
        val booksInDb = dbHelper.getBooksBySubjectId(subjects[0].id)
        val book = dbHelper.getBookById(booksInDb[0].id)

        assertEquals("The Lord of the Rings", book.title)
    }
}