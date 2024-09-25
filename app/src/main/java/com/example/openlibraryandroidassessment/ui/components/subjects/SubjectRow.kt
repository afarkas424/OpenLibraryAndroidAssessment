package com.example.openlibraryandroidassessment.ui.components.subjects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.openlibraryandroidassessment.data.models.Subject
import com.example.openlibraryandroidassessment.ui.theme.Typography


/**
 * Subject Row stateless composable
 */
@Composable
fun SubjectRow(
    name: String,
    count: String,
    subjectID: String,
    navigateToBookScreen: (String) -> Unit
) {
    // Row layout for the subject
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                // Navigate to the books screen for the selected subject
                navigateToBookScreen.invoke("books/${subjectID}")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Text View For Subject Name
        Text(
            text = name,
            style = Typography.bodyLarge,
            color = Color.Black
        )

        // Extra row to contain chevron and count to accommodate spacing to match UI Spec
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Count of books
            Text(
                text = count,
                style = Typography.bodyLarge,
                modifier = Modifier.padding(end = 8.dp),
                color = Color.Black
            )
            // Chevron icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun SubjectRowPreview() {
    val populatedSubject = Subject(id = 1, name = "Science Fiction", count = 37)
    SubjectRow(
        name = populatedSubject.name,
        count = populatedSubject.count.toString(),
        subjectID = populatedSubject.id.toString()
    ) { }
}
