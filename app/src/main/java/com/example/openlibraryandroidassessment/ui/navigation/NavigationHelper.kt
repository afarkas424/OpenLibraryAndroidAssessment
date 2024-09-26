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
 * Builds navigation graph. Utilizes route parameters to inform view model of
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
            // inform view model which subject was clicked
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
            // inform view model which book was clicked
            viewModel.onBookClicked(bookId.toInt())
            BookDetailsScreen(
                bookDetails = viewModel.bookDetails,
                navController = navHostController
            )
        }
    }
}