package com.example.openlibraryandroidassessment.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.openlibraryandroidassessment.ui.components.bookDetails.BookDetailsScreen
import com.example.openlibraryandroidassessment.ui.components.books.BooksScreen
import com.example.openlibraryandroidassessment.ui.components.subjects.SubjectsScreen
import com.example.openlibraryandroidassessment.viewmodels.OpenLibraryViewModel

/**
 * Configures the app's navigation controller. Utilizes route parameters to inform view model of
 * selected subject or book.
 */
@Composable
fun NavigationSetup(
    navHostController: NavHostController,
    viewModel: OpenLibraryViewModel
) {
    // Initially start at subjects screen
    NavHost(navHostController, startDestination = "subjects") {
        composable("subjects") {
            SubjectsScreen(
                navigateToBookScreen = { route:String -> navHostController.navigate(route) },
                subjectLiveData = viewModel.subjectList
            )
        }
        composable("books/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""

            // todo: could improve this, not sure if navController should trigger anything in view model
            viewModel.onSubjectClicked(subjectId.toInt())

            BooksScreen(
                navigateBack = { navHostController.popBackStack() },
                navigateToBookDetails = { route:String -> navHostController.navigate(route) },
                booksLiveData = viewModel.bookList,
                subjectNameLiveData = viewModel.selectedSubjectTitle
            )
        }
        composable("bookDetails/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            // todo: send selected ID to the desired View Model to post to shared flow
            viewModel.onBookClicked(bookId.toInt())
            BookDetailsScreen(
                bookDetails = viewModel.bookDetails,
                navController = navHostController
            )
        }
    }
}