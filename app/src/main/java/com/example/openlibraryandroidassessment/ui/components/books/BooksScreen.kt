package com.example.openlibraryandroidassessment.ui.components.books

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import com.example.openlibraryandroidassessment.data.models.Book
import com.example.openlibraryandroidassessment.ui.components.subjects.SubjectsScreen

@Composable
fun BooksScreen(
    /**book view model*/
    navigateBack: () -> Unit,
    navigateToBookDetails: (route: String) -> Unit,
    booksLiveData: LiveData<List<Book>>
) {
    // TODO: Retrieve books for the given subject ID and display them in a list
//    val populatedBook = Book(
//        id = 1,
//        title = "Infinite Jest",
//        authors = "David Foster Wallace",
//        imageURLBase = "https://covers.openlibrary.org/b/id/10071047",
//        detailsKey = "",
//        publishedYear = 1999
//
//    )
//    val populatedBook2 = Book(
//        id = 2,
//        title = "1984",
//        authors = "David Foster Wallace",
//        detailsKey = "",
//        imageURLBase = "https://covers.openlibrary.org/b/id/12919048",
//        publishedYear = 1984
//    )
//    val books = listOf(
//        populatedBook, populatedBook2
//    )

    val books = booksLiveData.observeAsState().value
    if (books != null) {
        BooksScreen(
            books = books,
            navigateBack = navigateBack,
            navigateToBookDetails = navigateToBookDetails
        )
    } else {
        // Todo: Make a loading spinner instead of text
        Text("loading books...")
    }


}

/**
 * Stateless subject screen composable for the list of books, for previews
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
    books: List<Book>,
    navigateBack: () -> Unit,
    navigateToBookDetails: (route:String) -> Unit
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text("Books") },
            navigationIcon = {
                IconButton(onClick = { navigateBack.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }
        )
        LazyColumn {
            items(books) { book ->
                BookRow(
                    book = book,
                    navigateToBookDetails = navigateToBookDetails
                )
            }
        }

    }
}

@Preview
@Composable
fun BooksScreenPreview() {
    val populatedBook = Book(
        id = 1,
        title = "Infinite Jest",
        authors = "David Foster Wallace",
        imageURLBase = "https://covers.openlibrary.org/b/id/10071047",
        detailsKey = "",
        publishedYear = 1999

    )
    val populatedBook2 = Book(
        id = 2,
        title = "1984",
        authors = "David Foster Wallace",
        detailsKey = "",
        imageURLBase = "https://covers.openlibrary.org/b/id/12919048",
        publishedYear = 1984
    )
    val books = listOf(
        populatedBook, populatedBook2
    )

    BooksScreen(
        books = books,
        navigateBack = {},
        navigateToBookDetails = {}
    )

}