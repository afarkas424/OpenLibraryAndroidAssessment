import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(message: String) {
    // Full-screen layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Loading spinner
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp) // Adjust size as needed
            )

            Spacer(modifier = Modifier.height(16.dp)) // Space between spinner and text box

            // Text box
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
