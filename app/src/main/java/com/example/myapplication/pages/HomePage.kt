package com.example.myapplication.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.AuthState
import com.example.myapplication.AuthViewModel
import com.example.myapplication.R
import com.example.myapplication.Todo
import com.example.myapplication.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    val todoViewModel: TodoViewModel = viewModel() // Koristi ViewModelProvider za instanciranje ViewModel-a

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Sign out button positioned at the top right
        TextButton(
            onClick = { authViewModel.signout() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Sign out")
        }

        // Column for the rest of the content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp) // Adjust padding to ensure content is not obscured by the button
        ) {
            // Todo list positioned in the center
            TodoListPage(viewModel = todoViewModel) // Prosleđivanje instance TodoViewModel
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListPage(viewModel: TodoViewModel) {
    val todoList by viewModel.todoList.observeAsState()
    var inputText by remember { mutableStateOf("") }
    var editingItem by remember { mutableStateOf<Todo?>(null) } // Stanje za trenutno uređivanje
    var showError by remember { mutableStateOf(false) } // Stanje za prikaz greške

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    showError = false // Sakrij grešku kad korisnik menja unos
                },
                isError = showError, // Postavi grešku ako je potrebno
                placeholder = { Text("Enter a todo item") }
            )
            Button(
                onClick = {
                    if (inputText.trim().isNotEmpty()) {
                        viewModel.addTodo(inputText)
                        inputText = ""
                        showError = false
                    } else {
                        showError = true // Prikaz greške ako je unos prazan
                    }
                }
            ) {
                Text(text = "Add")
            }
        }

        if (showError) {
            Text(
                text = "Todo item cannot be empty",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        todoList?.let {
            LazyColumn(
                content = {
                    itemsIndexed(it) { index: Int, item: Todo ->
                        TodoItem(
                            item = item,
                            isEditing = editingItem == item,
                            onEdit = { editingItem = item },
                            onSave = { newText ->
                                viewModel.updateTodo(item.copy(title = newText)) // Ažuriraj naziv stavke
                                editingItem = null // Zatvori uređivanje
                            },
                            onCancel = {
                                editingItem = null // Zatvori uređivanje bez spremanja
                            },
                            onDelete = {
                                viewModel.deleteTodo(item.id)
                            }
                        )
                    }
                }
            )
        } ?: Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "No items yet",
            fontSize = 16.sp
        )
    }
}

@Composable
fun TodoItem(
    item: Todo,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    var editText by remember { mutableStateOf(item.title) } // Za praćenje unosa uređivanja
    var showDialog by remember { mutableStateOf(false) } // Stanje za prikaz dijaloga

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = SimpleDateFormat("HH:mm:aa, dd/MM", Locale.ENGLISH).format(item.createdAt),
                fontSize = 12.sp,
                color = Color.LightGray
            )
            if (isEditing) {
                // Prikazuj OutlinedTextField kad uređujemo stavku
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Row {
                    IconButton(onClick = { onSave(editText) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_check_24), // Zamijenite s ikonom kvačice
                            contentDescription = "Save"
                        )
                    }
                    IconButton(onClick = onCancel) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cancel_24), // Zamijenite s ikonom za poništavanje
                            contentDescription = "Cancel"
                        )
                    }
                }
            } else {
                // Prikazuj samo naziv stavke kad ne uređujemo
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.clickable { onEdit() } // Klik za uređivanje
                )
            }
        }

        // Ikonica za zvono
        IconButton(onClick = { showDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_notifications_24), // Zamijenite s ikonom zvona
                contentDescription = "Notify"
            )
        }

        // Prikaz dijaloga za odabir vremena
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Odaberite vrijeme") },
                text = {
                    Column {
                        TextButton(onClick = { /* logika za 1 sat */ showDialog = false }) {
                            Text("1 sat")
                        }
                        TextButton(onClick = { /* logika za 12 sati */ showDialog = false }) {
                            Text("12 sati")
                        }
                        TextButton(onClick = { /* logika za 24 sata */ showDialog = false }) {
                            Text("24 sata")
                        }
                    }
                },
                confirmButton = {}
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete"
            )
        }
    }
}
