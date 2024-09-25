package com.example.openlibraryandroidassessment.ui.components.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.openlibraryandroidassessment.R
import com.example.openlibraryandroidassessment.data.models.Book


/**
 * Composable for showing a books cover, title, and author with no navigation for preview
 */
@Composable
fun BookRow(
    book: Book,
    navigateToBookDetails: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { navigateToBookDetails.invoke("bookDetails/${book.id}")},
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Async image for the book cover
        AsyncImage(
            model = book.imageURLBase.toString().plus("-S.jpg"),
            contentDescription = "Book cover",
            modifier = Modifier
                .size(60.dp) // Adjust size as needed
                .padding(end = 16.dp),
            error = painterResource(id = R.drawable.image_placeholder),
            placeholder = painterResource(id = R.drawable.image_placeholder)
        )

        // Column for title and author
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = book.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "${book.publishedYear}, ${book.authors}",
                color = Color.Gray
            )
        }

        // Chevron icon to indicate navigation
        Icon (
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}

@Preview
@Composable
fun BookRowPreview() {
    val populatedBook = Book(
        id = 1,
        title = "Infinite Jest",
        authors = "David Foster Wallace",
        imageURLBase = "https://covers.openlibrary.org/b/id/10071047",
        detailsKey = "",
        publishedYear = 1999

    )
    BookRow(
        book = populatedBook,
        navigateToBookDetails = {}
    )
}