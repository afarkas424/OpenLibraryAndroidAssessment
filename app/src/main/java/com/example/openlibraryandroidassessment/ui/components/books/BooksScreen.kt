package com.example.openlibraryandroidassessment.ui.components.books

import LoadingScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.openlibraryandroidassessment.data.models.Book

/**
 * Stateful subject screen composable
 */
@Composable
fun BooksScreen(
    navigateBack: () -> Unit,
    navigateToBookDetails: (route: String) -> Unit,
    booksLiveData: LiveData<List<Book>>,
    subjectNameLiveData: LiveData<String>
) {
    val books = booksLiveData.observeAsState().value
    val subjectName = subjectNameLiveData.observeAsState().value
    if (books != null) {
        BooksScreen(
            books = books,
            navigateBack = navigateBack,
            navigateToBookDetails = navigateToBookDetails,
            subjectName = subjectName ?: "Books"
        )
    } else {
        LoadingScreen("Please allow 12-13 seconds to load OpenLibrary data")
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
    navigateToBookDetails: (route: String) -> Unit,
    subjectName: String
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text(
                text = subjectName,
                fontWeight = FontWeight.Bold
            ) },
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
                HorizontalDivider(
                    modifier = Modifier,
                    thickness = 1.dp,
                    color = Color.Gray
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
        navigateToBookDetails = {},
        subjectName = "Science Fiction"
    )

}