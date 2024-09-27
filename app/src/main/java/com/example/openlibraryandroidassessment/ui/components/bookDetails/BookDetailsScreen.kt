package com.example.openlibraryandroidassessment.ui.components.bookDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.openlibraryandroidassessment.R
import com.example.openlibraryandroidassessment.data.models.BookDetailsScreenInformation
import com.example.openlibraryandroidassessment.ui.theme.Typography

/**
 * Stateful composable for Displaying the list of books relevant to the selected subject
 */
@Composable
fun BookDetailsScreen(
    bookDetails: LiveData<BookDetailsScreenInformation>,
    navigateBack: () -> Unit
) {
    // observe on book details
    val details = bookDetails.observeAsState().value
    if (details != null) {
        BookDetailsScreen(
            imageUrl = details.imgURL,
            text = details.description,
            title = details.title,
            navigateBack = navigateBack
        )
    }

}

/**
 * Stateless book details composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    imageUrl: String,
    text: String,
    title: String,
    navigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navigateBack.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Large Image
            AsyncImage(
                model = imageUrl,
                contentDescription = "Book Cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // fixed height of 300
                error = painterResource(id = R.drawable.image_placeholder),
                placeholder = painterResource(id = R.drawable.image_placeholder)
            )

            // Large Body of Text
            Text(
                text = text,
                style = Typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp) // Add some space above the text
            )
        }
    }
}

@Preview
@Composable
fun BookDetailsScreenPreview() {
    val bookDetails = BookDetailsScreenInformation(
        imgURL = "https://covers.openlibrary.org/b/id/10071047-L.jpg",
        description = "A gargantuan, mind-altering comedy about the Pursuit of Happiness in America Set in an addicts' halfway house and a tennis academy, and featuring the most endearingly screwed-up family to come along in recent fiction, Infinite Jest explores essential questions about what entertainment is and why it has come to so dominate our lives; about how our desire for entertainment affects our need to connect with other people; and about what the pleasures we choose say about who we are. Equal parts philosophical quest and screwball comedy, Infinite Jest bends every rule of fiction without sacrificing for a moment its own entertainment value. It is an exuberant, uniquely American exploration of the passions that make us human - and one of those rare books that renew the idea of what a novel can do. ",
        title = "Infinite Jest",
    )

    BookDetailsScreen(
        imageUrl = bookDetails.imgURL,
        text = bookDetails.description,
        title = bookDetails.title,
        navigateBack = {}
    )

}