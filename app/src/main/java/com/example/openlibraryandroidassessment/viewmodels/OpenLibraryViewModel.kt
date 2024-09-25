package com.example.openlibraryandroidassessment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openlibraryandroidassessment.data.models.Book
import com.example.openlibraryandroidassessment.data.models.BookDetailsScreenInformation
import com.example.openlibraryandroidassessment.data.models.Subject
import com.example.openlibraryandroidassessment.data.repositories.BookDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OpenLibraryViewModel(
    private val bookRepo: BookDataRepo
) : ViewModel() {

    /**
     * Live data for the list of subjects to display on screen one
     */
    val subjectList: LiveData<List<Subject>> = bookRepo.subjectList

    val bookList: LiveData<List<Book>> = bookRepo.bookList

    val selectedSubjectTitle: LiveData<String> = bookRepo.selectedSubjectTitle

    val bookDetails: LiveData<BookDetailsScreenInformation> = bookRepo.bookDetails

    fun onSubjectClicked(subjectId: Int) {
        // inform the repo of the selected subject and load desired books
        viewModelScope.launch(Dispatchers.IO) {
            bookRepo.getBooksForSelectedSubjectFromLocalDatabase(subjectId)
        }


    }

    fun onBookClicked(bookID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bookRepo.retrieveAndPostBookDetailsScreenData(bookID)
        }

    }




}