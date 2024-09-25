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

    val selectedSubjectTitle: LiveData<String> = bookRepo.selectedSubjectTitle

    val bookDetails: LiveData<String> = bookRepo.bookDetails

    fun onSubjectClicked(subjectId: Int) {
        // inform the repo of the selected subject and load desired books
        bookRepo.getBooksForSelectedSubjectFromLocalDatabase(subjectId)

        // get name of selected subject and post to view model

    }

    fun onBookClicked(bookID: Int) {

    }




}