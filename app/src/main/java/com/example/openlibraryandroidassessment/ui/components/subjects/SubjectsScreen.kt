package com.example.openlibraryandroidassessment.ui.components.subjects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.openlibraryandroidassessment.data.models.Subject

/**
 * Stateful subject screen composable for the list of subjects with navigation
 */
@Composable
fun SubjectsScreen(/**viewmodel.subjectsFlow*/ navigateToBookScreen: (String) -> Unit) {
    // Retrieve books for the given subject ID and display them in a list

    // todo: collect the list of subjects as a state
    val subjects = listOf(
        Subject( id = 1, name = "Science Fiction", count = 37),
        Subject( id = 2, name = "True Crime", count = 42)
    )
    SubjectsScreen(
        subjects = subjects,
        navigateToBookScreen = navigateToBookScreen
    )
}

/**
 * Stateless subject screen composable for the list of subjects, for previews
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(subjects: List<Subject>, navigateToBookScreen: (String) -> Unit) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text("Subjects") },
        )
        LazyColumn {
            items(subjects) { subject ->
                SubjectRow(
                    name = subject.name,
                    count = subject.count.toString(),
                    subjectID = subject.id.toString(),
                    navigateToBookScreen = navigateToBookScreen
                )
            }
        }

    }
}

@Preview
@Composable
fun SubjectsScreenPreview() {
    val subjects = listOf(
        Subject( id = 1, name = "Science Fiction", count = 37),
        Subject( id = 2, name = "True Crime", count = 42)
    )
    SubjectsScreen(
        subjects = subjects,
        navigateToBookScreen = {}
    )

}