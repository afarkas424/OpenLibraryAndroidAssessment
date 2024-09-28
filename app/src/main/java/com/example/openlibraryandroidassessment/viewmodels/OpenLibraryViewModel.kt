package com.example.openlibraryandroidassessment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openlibraryandroidassessment.data.models.Book
import com.example.openlibraryandroidassessment.data.models.BookDetailsScreenInformation
import com.example.openlibraryandroidassessment.data.models.NavigationEvent
import com.example.openlibraryandroidassessment.data.models.Subject
import com.example.openlibraryandroidassessment.data.repositories.OpenLibraryDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Shared view model for all screens
 *
 * @param bookRepo the BookDataRepo
 */
class OpenLibraryViewModel(
    private val bookRepo: OpenLibraryDataRepo
) : ViewModel() {

    /**
     * Live data for navigation event. When observed, will trigger view models response to user navigation.
     */
    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent

    /**
     * Reference to live data for the list of subjects to display on subject screen
     */
    val subjectList: LiveData<List<Subject>> = bookRepo.subjectList

    /**
     * Reference to live data for the list of books to display on books screen
     */
    val bookList: LiveData<List<Book>> = bookRepo.bookList

    /**
     * Reference to live data for the subject name to be displayed on books screen
     */
    val selectedSubjectTitle: LiveData<String> = bookRepo.selectedSubjectTitle

    /**
     * Reference to live data for information to be displayed on the book details screen
     */
    val bookDetails: LiveData<BookDetailsScreenInformation> = bookRepo.bookDetails

    /**
     * Called when a subject on the subject screen is clicked
     *
     * @param subjectId, the integer ID of the clicked subject
     */
    fun onSubjectClicked(subjectId: Int) {
        // trigger navigation to books screen
        _navigationEvent.postValue(NavigationEvent.NavigateToBooksScreen)

        // inform the repo of the selected subject and load desired books
        viewModelScope.launch(Dispatchers.IO) {
            bookRepo.getBooksForSelectedSubjectFromLocalDatabase(subjectId)
        }
    }

    /**
     * Called when a book is clicked on the book screen
     *
     * @param bookId, the integer ID of the clicked book
     */
    fun onBookClicked(bookID: Int) {
        // trigger navigation to book details screen
        _navigationEvent.postValue(NavigationEvent.NavigateToBookDetailsScreen)

        // retrieve book details and post to screen
        viewModelScope.launch(Dispatchers.IO) {
            bookRepo.retrieveAndPostBookDetailsScreenData(bookID)
        }
    }

    /**
     * Called when the user selects the back button from book details page.
     */
    fun onBackToBooksClicked() {
        // trigger navigation to books screen
        _navigationEvent.postValue(NavigationEvent.NavigateToBooksScreen)
    }

    /**
     * Called when the user selects the back button from book details page.
     */
    fun onBackToSubjectsClicked() {
        // trigger back navigation
        _navigationEvent.postValue(NavigationEvent.NavigateToSubjectsScreen)
    }
}