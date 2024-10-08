import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.myapplication.AuthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@Composable
fun Progress(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var subjects by remember { mutableStateOf(listOf<String>()) }
    var newSubject by remember { mutableStateOf(TextFieldValue("")) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var notesMap by remember { mutableStateOf(mutableMapOf<String, TextFieldValue>()) }

    // Function to delete a subject
    fun deleteSubject(subject: String) {
        subjects = subjects.filter { it != subject }
        notesMap.remove(subject)
    }

    if (selectedSubject == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Subjects", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            // List of subjects
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(subjects) { subject ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subject,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSubject = subject }
                        )
                        IconButton(
                            onClick = { deleteSubject(subject) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input field to add a new subject
            TextField(
                value = newSubject,
                onValueChange = { newSubject = it },
                label = { Text("New Subject") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Button to add the new subject
            Button(onClick = {
                if (newSubject.text.isNotBlank()) {
                    subjects = subjects + newSubject.text
                    newSubject = TextFieldValue("")
                }
            }) {
                Text("Add Subject")
            }
        }
    } else {
        // Notes screen for the selected subject

        // Retrieve or initialize the notes for the selected subject
        var notes by remember(selectedSubject) {
            mutableStateOf(notesMap[selectedSubject] ?: TextFieldValue(""))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Notes for $selectedSubject", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            // TextField to input notes
            TextField(
                value = notes,
                onValueChange = {
                    notes = it
                    notesMap[selectedSubject!!] = it  // Update the map with new notes
                },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxHeight(0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to go back to the subject list
            Button(onClick = { selectedSubject = null }) {
                Text("Back to Subjects")
            }
        }
    }
}
