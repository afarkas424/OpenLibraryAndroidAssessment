package com.example.openlibraryandroidassessment.data.models

/**
 * Sealed class to enumerate the different navigation actions that the user can trigger
 */
sealed class NavigationEvent {

    /**
     * Navigation event for when user selects a subject
     */
    data object NavigateToBooksScreen : NavigationEvent()

    /**
     * Navigation event for when user selects a book
     */
    data object NavigateToBookDetailsScreen: NavigationEvent()

    /**
     * Navigation event for when user selects the back arrow from the bookDetailsScreen
     */
    data object NavigateToSubjectsScreen : NavigationEvent()

}