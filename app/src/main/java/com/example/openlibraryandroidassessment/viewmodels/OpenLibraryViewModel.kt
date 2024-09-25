package com.example.openlibraryandroidassessment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.openlibraryandroidassessment.data.models.Book
import com.example.openlibraryandroidassessment.data.models.Subject
import com.example.openlibraryandroidassessment.data.repositories.BookDataRepo

class OpenLibraryViewModel(
    private val bookRepo: BookDataRepo
) : ViewModel() {

    /**
     * Live data for the list of subjects to display on screen one
     */
    val subjectList: LiveData<List<Subject>> = bookRepo.subjectList

    val bookList: LiveData<List<Book>> = bookRepo.bookList

    val bookDetails: LiveData<String> = bookRepo.bookDetails

    fun onSubjectClicked(subjectId: Int) {
        // inform the repo of the selected subject and load desired books
        bookRepo.getBooksForSelectedSubjectFromLocalDatabase(subjectId)
    }

    fun onBookClicked(bookID: Int) {

    }




}