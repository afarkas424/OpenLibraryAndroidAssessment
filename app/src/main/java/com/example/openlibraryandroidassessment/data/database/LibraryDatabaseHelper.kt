package com.example.openlibraryandroidassessment.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.openlibraryandroidassessment.data.models.BookData

class LibraryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "openLibrary.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // table of only subjects
        val createSubjectsTable =
            "CREATE TABLE subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)"
        db.execSQL(createSubjectsTable)

        // table of only books
        val createBooksTable = "CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,authors TEXT,image_num LONG,published_year LONG,details_key TEXT)"
        db.execSQL(createBooksTable)

        // table of books and subjects
        val createBookSubjectTable = "CREATE TABLE book_subject (book_id INTEGER,subject_id INTEGER,FOREIGN KEY (book_id) REFERENCES books(id), FOREIGN KEY (subject_id) REFERENCES subjects(id))"
        db.execSQL(createBookSubjectTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS books")
        db.execSQL("DROP TABLE IF EXISTS subjects")
        db.execSQL("DROP TABLE IF EXISTS book_subject")
        onCreate(db)
    }

//    fun insertBooksAndSubjects(books: List<BookData>) {
//        val db = writableDatabase
//        db.beginTransaction() // Start the transaction
//
//        try {
//            books.forEach { book ->
//                // insert book values into table
//                val bookValues = ContentValues().apply {
//                    put("title", book.title)
//                    // write authors as "author 1, author 2, ...."
//                    put("authors", book.author_name.joinToString(separator = ", "))
//                    put("image_num", book.cover_i)
//                    put("published_year", book.first_publish_year)
//                    put("details_key", book.key)
//                }
//
//                val bookId = db.insert("books", null, bookValues)
//                for (subject in book.subject) {
//                    // add the subject to subject table
//                    val subjectId = insertOrGetSubjectId(db, subject)
//                    // add subject and book id to joint table
//                    val bookSubjectValues = ContentValues().apply {
//                        put("book_id", bookId)
//                        put("subject_id", subjectId)
//                    }
//                    db.insert("book_subject", null, bookSubjectValues)
//                }
//            }
//        } catch (e: Exception) {
//            // Handle any exceptions as needed
//        } finally {
//            db.endTransaction() // End the transaction
//        }
//    }

//    private fun insertOrGetSubjectId(db: SQLiteDatabase, subject: String): Long {
//        val cursor =
//            db.query("subjects", arrayOf("id"), "name = ?", arrayOf(subject), null, null, null)
//        return if (cursor.moveToFirst()) {
//            // if subject exists, return its id (column 0)
//            cursor.getLong(0)
//        } else {
//            // otherwise insert new subject
//            val subjectValues = ContentValues().apply {
//                put("name", subject)
//            }
//            db.insert("subjects", null, subjectValues)
//        }.also { cursor.close() }
//    }

    fun insertBooksAndSubjects(books: List<BookData>) {
        val db = writableDatabase
        db.beginTransaction() // Start the transaction

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
                        db.insert("book_subject", null, bookSubjectValues)
                    }
                }
            }

            db.setTransactionSuccessful() // Mark the transaction as successful
        } catch (e: Exception) {
            // Handle any exceptions as needed
        } finally {
            db.endTransaction() // End the transaction
        }
    }

    fun getBooksGroupedBySubject(): Map<String, List<String>> {
        val db = writableDatabase
        val query = """
        SELECT subjects.name AS subject, books.title AS title
        FROM subjects
        JOIN book_subject ON subjects.id = book_subject.subject_id
        JOIN books ON books.id = book_subject.book_id
    """

        val cursor = db.rawQuery(query, null)
        val resultMap = mutableMapOf<String, MutableList<String>>()

        while (cursor.moveToNext()) {
            val subjectColumnIndex = cursor.getColumnIndex("subject")
            val titleColumnIndex = cursor.getColumnIndex("title")
            if ( subjectColumnIndex != -1 && titleColumnIndex != -1) {
                val subject = cursor.getString(subjectColumnIndex)
                val title = cursor.getString(titleColumnIndex)

                resultMap.computeIfAbsent(subject) { mutableListOf() }.add(title)
            }
        }
        cursor.close()

        return resultMap
    }
}


