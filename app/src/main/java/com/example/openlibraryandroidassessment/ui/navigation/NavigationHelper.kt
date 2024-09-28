package com.example.openlibraryandroidassessment.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.openlibraryandroidassessment.data.models.NavigationEvent
import com.example.openlibraryandroidassessment.ui.components.bookDetails.BookDetailsScreen
import com.example.openlibraryandroidassessment.ui.components.books.BooksScreen
import com.example.openlibraryandroidassessment.ui.components.subjects.SubjectsScreen
import com.example.openlibraryandroidassessment.viewmodels.OpenLibraryViewModel

/**
 * Builds navigation graph.
 */
@Composable
fun NavigationGraph(
    viewModel: OpenLibraryViewModel
) {
    // Instantiate navigation controller
    val navController = rememberNavController()

    // Observe on navigation events
    viewModel.navigationEvent.observeAsState().value?.let { navigationEvent: NavigationEvent ->
        when (navigationEvent) {
            is NavigationEvent.NavigateToBooksScreen -> {
                navController.navigate("books")
            }

            is NavigationEvent.NavigateToBookDetailsScreen -> {
                navController.navigate("bookDetails")
            }

            is NavigationEvent.NavigateToSubjectsScreen -> {
                navController.navigate("subjects")
            }
        }
    }


    // Initially start at subjects screen
    NavHost(navController, startDestination = "subjects") {
        composable("subjects") {
            SubjectsScreen(
                navigateToBookScreen = viewModel::onSubjectClicked,
                subjectLiveData = viewModel.subjectList
            )
        }
        composable("books") {
            BooksScreen(
                navigateBack = viewModel::onBackToSubjectsClicked,
                navigateToBookDetails = viewModel::onBookClicked,
                booksLiveData = viewModel.bookList,
                subjectNameLiveData = viewModel.selectedSubjectTitle
            )
        }
        composable("bookDetails") {
            BookDetailsScreen(
                bookDetails = viewModel.bookDetails,
                navigateBack = viewModel::onBackToBooksClicked
            )
        }
    }
}