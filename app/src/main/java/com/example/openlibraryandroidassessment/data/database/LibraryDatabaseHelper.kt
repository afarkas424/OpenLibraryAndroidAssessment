package com.example.openlibraryandroidassessment.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.openlibraryandroidassessment.data.models.Book
import com.example.openlibraryandroidassessment.data.models.BookData
import com.example.openlibraryandroidassessment.data.models.Subject

class LibraryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "openLibrary.db"
        private const val DATABASE_VERSION = 1
    }

    // SQL Table Creation Strings
    private val CREATE_SUBJECTS_TABLE = "CREATE TABLE subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)"
    private val CREATE_BOOKS_TABLE = """
        CREATE TABLE books (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            authors TEXT,
            image_num NUM,
            published_year NUM,
            details_key TEXT
        )
    """
    private val CREATE_BOOK_SUBJECT_TABLE = """
        CREATE TABLE book_subject (
            book_id INTEGER,
            subject_id INTEGER,
            FOREIGN KEY (book_id) REFERENCES books(id),
            FOREIGN KEY (subject_id) REFERENCES subjects(id),
            PRIMARY KEY (book_id, subject_id)
        )
    """

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_SUBJECTS_TABLE)
        db.execSQL(CREATE_BOOKS_TABLE)
        db.execSQL(CREATE_BOOK_SUBJECT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS books")
        db.execSQL("DROP TABLE IF EXISTS subjects")
        db.execSQL("DROP TABLE IF EXISTS book_subject")
        onCreate(db)
    }

    fun insertBooksAndSubjects(books: List<BookData>) {
        // open db, begin transaction
        val db = writableDatabase
        db.beginTransaction()

        try {
            // Collect all unique subjects
            val subjectSet = mutableSetOf<String>()
            books.forEach { book ->
                subjectSet.addAll(book.subject)
            }

            // Fetch existing subjects
            val existingSubjects = mutableMapOf<String, Long>()
            val placeholders = subjectSet.joinToString(", ") { "?" }
            val cursor = db.query("subjects", arrayOf("id", "name"), "name IN ($placeholders)", subjectSet.toTypedArray(), null, null, null)
            while (cursor.moveToNext()) {
                existingSubjects[cursor.getString(1)] = cursor.getLong(0)
            }
            cursor.close()

            // Insert new subjects and prepare a map for IDs
            val newSubjectIds = mutableMapOf<String, Long>()
            subjectSet.forEach { subject ->
                if (!existingSubjects.containsKey(subject)) {
                    val subjectValues = ContentValues().apply {
                        put("name", subject)
                    }
                    val newId = db.insert("subjects", null, subjectValues)
                    newSubjectIds[subject] = newId
                }
            }

            // Now insert books and their subjects
            books.forEach { book ->
                val bookValues = ContentValues().apply {
                    put("title", book.title)
                    put("authors", book.author_name.joinToString(separator = ", "))
                    put("image_num", book.cover_i)
                    put("published_year", book.first_publish_year)
                    put("details_key", book.key)
                }

                val bookId = db.insert("books", null, bookValues)

                book.subject.forEach { subject ->
                    val subjectId = existingSubjects[subject] ?: newSubjectIds[subject] ?: -1
                    if (subjectId != -1L) {
                        val bookSubjectValues = ContentValues().apply {
                            put("book_id", bookId)
                            put("subject_id", subjectId)
                        }
                        try {
                            db.insert("book_subject", null, bookSubjectValues)
                        } catch (e: Exception) {
                            Log.e("LibraryDatabaseHelper", "Error inserting into book_subject: ${e.message}", e)
                        }

                    }
                }
            }
            // Complete transaction
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Handle any exceptions as needed
        } finally {
            db.endTransaction() // End the transaction
        }
    }

    fun getBooksBySubjectId(subjectId: Int): List<Book> {
        val db = writableDatabase
        val booksList = mutableListOf<Book>()

        // Query to retrieve books with the given subject_id
        val query = """
        SELECT b.id, b.title, b.authors, b.image_num, b.published_year, b.details_key
        FROM books b
        INNER JOIN book_subject bs ON b.id = bs.book_id
        WHERE bs.subject_id = ?
    """

        // Execute the query
        val cursor = db.rawQuery(query, arrayOf(subjectId.toString()))

        // Loop through the results
        if (cursor.moveToFirst()) {
            do {
                val idColumnIndex = cursor.getColumnIndex("id")
                val titleColumnIndex = cursor.getColumnIndex("title")
                val authorsColumnIndex = cursor.getColumnIndex("authors")
                val imageNumColumnIndex = cursor.getColumnIndex("image_num")
                val publishedYearColumnIndex = cursor.getColumnIndex("published_year")
                val detailsKeyColumnIndex = cursor.getColumnIndex("details_key")

                // Ensure column indices are valid (greater than -1)
                if (idColumnIndex != -1 && titleColumnIndex != -1 && authorsColumnIndex != -1
                    && imageNumColumnIndex != -1 && publishedYearColumnIndex != -1 && detailsKeyColumnIndex != -1) {

                    val book = Book(
                        id = cursor.getInt(idColumnIndex),
                        title = cursor.getString(titleColumnIndex),
                        authors = cursor.getString(authorsColumnIndex),
                        imageURLBase = "https://covers.openlibrary.org/b/id/${cursor.getInt(imageNumColumnIndex)}",
                        publishedYear = cursor.getInt(publishedYearColumnIndex),
                        detailsKey = cursor.getString(detailsKeyColumnIndex)
                    )
                    booksList.add(book)
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return booksList
    }

    fun getSubjectNameById(subjectId: Int): String? {
        val db = writableDatabase
        var subjectName: String? = null

        // Query to retrieve the subject name by its ID
        val query = "SELECT name FROM subjects WHERE id = ?"

        // Execute the query
        val cursor = db.rawQuery(query, arrayOf(subjectId.toString()))

        // Loop through the results
        if (cursor.moveToFirst()) {
            val nameColumnIndex = cursor.getColumnIndex("name")
            if (nameColumnIndex != -1) {
                subjectName = cursor.getString(nameColumnIndex)
            }
        }

        cursor.close()
        return subjectName
    }

    fun getBookById(bookId: Int): Book {
        val db = readableDatabase
        val cursor = db.query(
            "books",
            null,
            "id = ?",
            arrayOf(bookId.toString()),
            null,
            null,
            null
        )

        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
        val authors = cursor.getString(cursor.getColumnIndexOrThrow("authors"))
        val imageNum = cursor.getInt(cursor.getColumnIndexOrThrow("image_num"))
        val publishedYear = cursor.getInt(cursor.getColumnIndexOrThrow("published_year"))
        val detailsKey = cursor.getString(cursor.getColumnIndexOrThrow("details_key"))

        val book = Book(
            id = id,
            title = title,
            authors = authors,
            imageURLBase = imageNum.toString(),
            publishedYear = publishedYear,
            detailsKey = detailsKey
        )

        cursor.close()

        return book
    }

    fun wipeDataBase() {
        val db = writableDatabase
        db.execSQL("DELETE FROM books")
        db.execSQL("DELETE FROM subjects")
        db.execSQL("DELETE FROM book_subject")
    }

    fun groupBooksBySubjectAndMapToSubjectStruct(): Map<String, Subject> {
        val db = writableDatabase
        val query = """
        SELECT subjects.name AS subject,
               subjects.id AS subject_id,
               COUNT(books.id) AS book_count
        FROM subjects
        JOIN book_subject ON subjects.id = book_subject.subject_id
        JOIN books ON books.id = book_subject.book_id
        GROUP BY subjects.name, subjects.id
        HAVING COUNT(books.id) > 1
    """

        val cursor = db.rawQuery(query, null)
        val resultMap = mutableMapOf<String, Subject>()

        while (cursor.moveToNext()) {
            val subjectColumnIndex = cursor.getColumnIndex("subject")
            val subjectIDColumnIndex = cursor.getColumnIndex("subject_id")
            val countColumnIndex = cursor.getColumnIndex("book_count")
            if (subjectColumnIndex != -1 && countColumnIndex != -1) {
                val subject = cursor.getString(subjectColumnIndex)
                val count = cursor.getInt(countColumnIndex)
                val subjectId = cursor.getInt(subjectIDColumnIndex)

                resultMap[subject] = Subject(
                    name = subject,
                    count = count,
                    id = subjectId
                )
            }
        }
        cursor.close()

        return resultMap
    }
}