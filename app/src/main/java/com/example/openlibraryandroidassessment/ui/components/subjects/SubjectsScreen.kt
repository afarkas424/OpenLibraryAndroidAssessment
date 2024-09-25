package com.example.openlibraryandroidassessment.ui.components.subjects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.openlibraryandroidassessment.data.models.Subject

/**
 * Stateful subject screen composable for the list of subjects with navigation
 */
@Composable
fun SubjectsScreen(
    /**viewmodel.subjectsFlow*/
    navigateToBookScreen: (String) -> Unit,
    subjectLiveData: LiveData<List<Subject>>,
                   ) {
    val subjects = subjectLiveData.observeAsState().value
    if (subjects != null) {
        SubjectsScreen(
            subjects = subjects,
            navigateToBookScreen = navigateToBookScreen
        )
    } else {
        // Todo: Make a loading spinner instead of text
        Text("loading subjects")
    }

    
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